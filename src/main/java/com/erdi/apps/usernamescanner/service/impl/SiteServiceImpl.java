package com.erdi.apps.usernamescanner.service.impl;

import com.erdi.apps.usernamescanner.UsernameScannerApplication;
import com.erdi.apps.usernamescanner.dto.SiteResponseModel;
import com.erdi.apps.usernamescanner.exception.CustomHttpClientException;
import com.erdi.apps.usernamescanner.model.SiteModel;
import com.erdi.apps.usernamescanner.model.SourceModel;
import com.erdi.apps.usernamescanner.service.HttpClientService;
import com.erdi.apps.usernamescanner.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final HttpClientService httpClientService;

    @Override
    public SiteResponseModel findAll(String username) {
        long startTime = System.currentTimeMillis();
        var list = sendHttpRequest(username.replaceAll("[^a-zA-Z0-9-_.]/g", ""));
        long stopTime = System.currentTimeMillis();
        double elapsedTimeInSecond = stopTime - startTime;
        log.warn("Execution completed in {} ms", elapsedTimeInSecond);
        return new SiteResponseModel(username, list);
    }


    private List<SiteModel> sendHttpRequest(String username) {
        var resultList = new LinkedList<SiteModel>();
        var sourceModelList = UsernameScannerApplication.sourceModelList;
        List<CompletableFuture<HttpResponse<String>>> callResultList = httpClientService.makeConcurrentGetCall(username);
        try {
            for (int i = 0; i < sourceModelList.size(); i++) {
                HttpResponse<String> futureResponse = callResultList.get(i).get();
                if (futureResponse.statusCode() == 200 || futureResponse.statusCode() == 404) {
                    SiteModel siteModel = new SiteModel(
                            sourceModelList.get(i).getSiteName(),
                            getStatus(futureResponse, sourceModelList.get(i)),
                            sourceModelList.get(i).getSiteRegisterUrl().replace("{}", username),
                            sourceModelList.get(i).getSiteIconUrl());
                    resultList.add(siteModel);
                } else {
                    log.warn("User: {}, Site: {}, Status {}", username,
                            sourceModelList.get(i).getSiteName(),
                            futureResponse.statusCode());
                }

            }
            return resultList;

        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getCause().toString());
            throw new CustomHttpClientException(e.getMessage());
        }
    }

    private int getStatus(HttpResponse<String> response, SourceModel sourceModel) {
        if (Objects.isNull(sourceModel.getMessage())) {
            return response.statusCode();
        }
        return response.body().contains(sourceModel.getMessage()) ? HttpStatus.NOT_FOUND.value() : response.statusCode();
    }

}
