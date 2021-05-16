package com.erdi.apps.usernamescanner.service;


import com.erdi.apps.usernamescanner.dto.SiteListResponseModel;
import com.erdi.apps.usernamescanner.exception.CustomHttpClientException;
import com.erdi.apps.usernamescanner.exception.SourceInitializationException;
import com.erdi.apps.usernamescanner.source.Source;
import com.erdi.apps.usernamescanner.util.HttpClientUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class SiteService {

    private static final List<Source> sourceList;
    private static final String TARGET = "{}";

    static {
        InputStream sourceStream = SiteService.class.getResourceAsStream("/static/sources.json");
        if(sourceStream == null)
            throw new SourceInitializationException("Source init error! Can not find source location.");
        Source[] sources = new Gson().fromJson(new BufferedReader(new InputStreamReader(sourceStream)), Source[].class);
        sourceList = Arrays.asList(sources);
        log.info("Site init completed. Site Count: " + sourceList.size());
    }

    public List<SiteListResponseModel> findAll(String username) {
        try {
            return sendHttpRequest(username);
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomHttpClientException(e.getMessage());
        }
    }

    private List<HttpRequest> prepareAndBuildHttpRequest(String username) {
        List<HttpRequest> requestList = new ArrayList<>();
        for (Source s : sourceList) {
            String siteUrl = s.getSiteUrl().replace(TARGET, username);
            if (s.getBody() != null && s.getContentType() != null) {
                String body = s.getBody().replace("####", username);
                requestList.add(HttpClientUtil.buildPostRequest(siteUrl, s.getContentType(), body));
            } else {
                requestList.add(HttpClientUtil.buildGetRequest(siteUrl));
            }
        }
        return requestList;
    }

    private List<SiteListResponseModel> sendHttpRequest(String username) throws ExecutionException, InterruptedException {
        log.info("Async Start");
        ExecutorService executorService = Executors.newCachedThreadPool();

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .executor(executorService)
                .build();

        List<SiteListResponseModel> resultList = new LinkedList<>();
        List<HttpRequest> requestList = prepareAndBuildHttpRequest(username);
        List<CompletableFuture<HttpResponse<String>>> callResultList = HttpClientUtil.concurrentCall(httpClient, requestList);

        for (int i = 0; i < callResultList.size(); i++) {
            Source s = sourceList.get(i);
            HttpResponse<String> futureResponse = callResultList.get(i).get();
            SiteListResponseModel model = new SiteListResponseModel(
                    s.getSiteName().replace(TARGET, username),
                    futureResponse.statusCode(),
                    s.getSiteRegisterUrl().replace(TARGET, username),
                    s.getSiteIconUrl());
            if (s.getMessage() != null) {
                if (futureResponse.body().contains(s.getMessage())) {
                    model.setStatusCode(404);
                }
            }
            resultList.add(model);

        }
        executorService.shutdown();
        log.info("Async End");
        return resultList;
    }

}
