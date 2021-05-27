package com.erdi.apps.usernamescanner.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;


public class SiteModel {

    private String siteName;
    private int status;
    private String registerUrl;
    private final String siteIconUrl;
    private HttpResponse<String> response;
    private final String message;


    public SiteModel(String siteName, String registerUrl, String siteIconUrl, HttpResponse<String> response, String message) {
        this.siteName = siteName;
        this.registerUrl = registerUrl;
        this.siteIconUrl = siteIconUrl;
        this.response = response;
        this.message = message;
    }


    private int getStatusForPostSource(HttpResponse<String> response) {
        JsonObject convertedObject = new Gson().fromJson(response.body(), JsonObject.class);
        String message = "";
        if (siteName.equalsIgnoreCase("instagram")) {
            message = convertedObject.getAsJsonObject("errors").getAsJsonArray("username").get(0).getAsJsonObject().get("code").getAsString();
        } else if (siteName.equalsIgnoreCase("twitch")) {
            message = convertedObject.get("data").getAsJsonObject().get("isUsernameAvailable").getAsString();
        } else if (siteName.equalsIgnoreCase("snapchat")) {
            message = convertedObject.get("data").getAsJsonObject().get("status_code").getAsString();
        }
        return this.message.contains(message) ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value();

    }

    private int getStatusForGetSource(HttpResponse<String> response) {
        if (this.message != null) {
            return this.message.contains(response.body()) ? HttpStatus.NOT_FOUND.value() : response.statusCode();
        } else {
            return response.statusCode();
        }
    }


    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getStatus() {
        return response.request().method().equalsIgnoreCase("post") ? this.getStatusForPostSource(response) : this.getStatusForGetSource(response);
    }


    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getSiteIconUrl() {
        return siteIconUrl;
    }

    public void setResponse(HttpResponse<String> response) {
        this.response = response;
    }



}
