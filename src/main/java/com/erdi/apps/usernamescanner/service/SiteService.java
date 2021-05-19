package com.erdi.apps.usernamescanner.service;


import com.erdi.apps.usernamescanner.dto.SiteListModel;
import com.erdi.apps.usernamescanner.dto.SiteResponseModel;
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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Slf4j
@Service
public class SiteService {

    private static final List<Source> sourceList;
    private static final String TARGET = "{}";

    static {
        InputStream sourceStream = SiteService.class.getResourceAsStream("/static/sources.json");
        if (sourceStream == null)
            throw new SourceInitializationException("Source init error! Can not find source location.");
        Source[] sources = new Gson().fromJson(new BufferedReader(new InputStreamReader(sourceStream)), Source[].class);
        sourceList = Arrays.asList(sources);
        log.info("Site init completed. Site Count: " + sourceList.size());
    }

    public SiteResponseModel findAll(String username) {
        try {
            long startTime = System.nanoTime();
            var list = sendHttpRequest(username);
            long stopTime = System.nanoTime();
            double elapsedTimeInSecond = ((double) (stopTime - startTime)) / 1_000_000_000;
            var returnResult = new SiteResponseModel(list.size(), username, list, elapsedTimeInSecond);
            log.info("User: " + returnResult.getUser() + "Site Count: " + returnResult.getSiteCount() +
                    " Time: " + elapsedTimeInSecond);
            return returnResult;

        } catch (Exception e) {
            log.error(e.getCause().toString());
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

    private List<SiteListModel> sendHttpRequest(String username) throws ExecutionException, InterruptedException {

        List<SiteListModel> resultList = new LinkedList<>();
        List<HttpRequest> requestList = prepareAndBuildHttpRequest(username);
        List<CompletableFuture<HttpResponse<String>>> callResultList = HttpClientUtil.concurrentCall(requestList);

        for (int i = 0; i < callResultList.size(); i++) {
            Source s = sourceList.get(i);
            HttpResponse<String> futureResponse = callResultList.get(i).get();
            if (futureResponse.statusCode() == 200 || futureResponse.statusCode() == 404) {
                resultList.add(new SiteListModel(s, username, futureResponse));
            } else {
                log.error("User: " + username + " Site: " + s.getSiteName() + " Status: " + futureResponse.statusCode());
            }
        }

        return resultList;
    }

}
