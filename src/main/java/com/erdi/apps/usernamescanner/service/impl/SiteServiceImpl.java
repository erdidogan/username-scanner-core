package com.erdi.apps.usernamescanner.service.impl;

import com.erdi.apps.usernamescanner.UsernameScannerApplication;
import com.erdi.apps.usernamescanner.dto.SiteResponse;
import com.erdi.apps.usernamescanner.exception.CustomHttpClientException;
import com.erdi.apps.usernamescanner.model.SiteModel;
import com.erdi.apps.usernamescanner.model.SourceModel;
import com.erdi.apps.usernamescanner.service.HttpClientService;
import com.erdi.apps.usernamescanner.service.SiteService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final HttpClientService httpClientService;

    @Override
    public SiteResponse findAll(String username) {
        long startTime = System.nanoTime();
        var list = sendHttpRequest(username);
        long stopTime = System.nanoTime();
        double elapsedTimeInSecond = ((double) (stopTime - startTime)) / 1_000_000_000;
        var returnResult = new SiteResponse(username, list, elapsedTimeInSecond);
        log.info(returnResult.toString());
        return returnResult;
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
        return response.request().method().equalsIgnoreCase("post") ?
                this.getStatusForPostSource(response, sourceModel) : this.getStatusForGetSource(response, sourceModel);
    }


    private int getStatusForPostSource(HttpResponse<String> response, SourceModel sourceModel) {
        JsonObject convertedObject = new Gson().fromJson(response.body(), JsonObject.class);
        String message = "####";
        if (sourceModel.getSiteName().equalsIgnoreCase("instagram")) {
            JsonArray responseArray = convertedObject.getAsJsonObject("errors").getAsJsonArray("username");
            if(responseArray != null)
                message = responseArray.get(0).getAsJsonObject().get("code").getAsString();
        } else if (sourceModel.getSiteName().equalsIgnoreCase("twitch")) {
            message = convertedObject.get("data").getAsJsonObject().get("isUsernameAvailable").getAsString();
        } else if (sourceModel.getSiteName().equalsIgnoreCase("snapchat")) {
            message = convertedObject.get("reference").getAsJsonObject().get("status_code").getAsString();
        }
        return sourceModel.getMessage().contains(message) ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value();

    }

    private int getStatusForGetSource(HttpResponse<String> response, SourceModel sourceModel) {
        if (sourceModel.getMessage() != null) {
            return sourceModel.getMessage().contains(response.body()) ? HttpStatus.NOT_FOUND.value() : response.statusCode();
        } else {
            return response.statusCode();
        }
    }

}
