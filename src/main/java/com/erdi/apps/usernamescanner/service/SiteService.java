package com.erdi.apps.usernamescanner.service;

import com.erdi.apps.usernamescanner.dto.SiteModel;
import com.erdi.apps.usernamescanner.dto.SourceModel;
import com.erdi.apps.usernamescanner.dto.response.SiteResponseModel;
import com.erdi.apps.usernamescanner.exception.CustomHttpClientException;
import com.erdi.apps.usernamescanner.exception.SourceInitializationException;
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

    private static final List<SourceModel> sourceModel;
    private static final String TARGET = "{}";

    static {
        InputStream sourceStream = SiteService.class.getResourceAsStream("/static/sources.json");
        if (sourceStream == null)
            throw new SourceInitializationException("Source init error! Can not find source location.");
        SourceModel[] sourceModels = new Gson().fromJson(new BufferedReader(new InputStreamReader(sourceStream)), SourceModel[].class);
        sourceModel = Arrays.asList(sourceModels);
        log.info("Site init completed. Site Count: " + sourceModel.size());
    }

    public SiteResponseModel findAll(String username) {
        try {
            long startTime = System.nanoTime();
            var list = sendHttpRequest(username);
            long stopTime = System.nanoTime();
            double elapsedTimeInSecond = ((double) (stopTime - startTime)) / 1_000_000_000;
            var returnResult = new SiteResponseModel(list.size(), username, list, elapsedTimeInSecond);
            log.info(returnResult.toString());
            return returnResult;

        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getCause().toString());
            throw new CustomHttpClientException(e.getMessage());
        }
    }

    private List<HttpRequest> prepareAndBuildHttpRequest(String username) {
        List<HttpRequest> requestList = new ArrayList<>();
        HttpClientUtil clientUtil = new HttpClientUtil();
        for (SourceModel s : sourceModel) {
            String siteUrl = s.getSiteUrl().replace(TARGET, username);
            if (s.getBody() != null && s.getContentType() != null) {
                String body = s.getBody().replace("####", username);
                requestList.add(clientUtil.buildPostRequest(siteUrl, s.getContentType(), body));
            } else {
                requestList.add(clientUtil.buildGetRequest(siteUrl));
            }
        }
        return requestList;
    }

    private List<SiteModel> sendHttpRequest(String username) throws ExecutionException, InterruptedException {
        HttpClientUtil clientUtil = new HttpClientUtil();
        List<SiteModel> resultList = new LinkedList<>();
        List<HttpRequest> requestList = prepareAndBuildHttpRequest(username);
        List<CompletableFuture<HttpResponse<String>>> callResultList = clientUtil.concurrentCall(requestList);

        for (int i = 0; i < callResultList.size(); i++) {
            SourceModel s = sourceModel.get(i);
            HttpResponse<String> futureResponse = callResultList.get(i).get();
            if (futureResponse.statusCode() == 200 || futureResponse.statusCode() == 404) {
                SiteModel siteModel = new SiteModel(s.getSiteName()
                        , s.getSiteRegisterUrl().replace("{}", username), s.getSiteIconUrl(), futureResponse, s.getMessage());
                resultList.add(siteModel);
            } else {
                log.error("User: {}, Site: {}, Future Response {}", username, s.getSiteName(), futureResponse.body());
            }
        }
        return resultList;
    }

}
