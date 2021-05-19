package com.erdi.apps.usernamescanner.dto;

import com.erdi.apps.usernamescanner.source.Source;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;

@Setter
@Getter
public class SiteListModel {

    private String siteName;
    private int status;
    private String registerUrl;
    private String siteIconUrl;


    public SiteListModel(Source source, String userName, HttpResponse<String> response) {
        this.setSiteName(source.getSiteName());
        this.setRegisterUrl(source.getSiteRegisterUrl().replace("{}", userName));
        this.setSiteIconUrl(source.getSiteIconUrl());
        this.setStatus(response.statusCode());
        if (response.request().method().equalsIgnoreCase("post")) {
            this.setStatus(this.getStatusForPostSource(source, response));
        } else {
            this.setStatus(this.getStatusForGetSource(source, response));
        }
    }


    private int getStatusForPostSource(Source source, HttpResponse<String> response) {
        JsonObject convertedObject = new Gson().fromJson(response.body(), JsonObject.class);
        String message = "";
        if (siteName.equalsIgnoreCase("instagram")) {
            message = convertedObject.getAsJsonObject("errors").getAsJsonArray("username").get(0).getAsJsonObject().get("code").getAsString();
        } else if (siteName.equalsIgnoreCase("twitch")) {
            message = convertedObject.get("data").getAsJsonObject().get("isUsernameAvailable").getAsString();
        }
        return source.getMessage().contains(message) ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value();

    }


    private int getStatusForGetSource(Source source, HttpResponse<String> response) {
        if (source.getMessage() != null) {
            return source.getMessage().contains(response.body()) ? HttpStatus.NOT_FOUND.value() : HttpStatus.OK.value();
        } else {
            return HttpStatus.OK.value();
        }
    }
}
