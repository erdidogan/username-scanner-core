package com.erdi.apps.usernamescanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SourceModel {

    private String siteName;
    private String siteUrl;
    private String siteIconUrl;
    private String siteRegisterUrl;
    private String message;
    private String contentType;
    private String body;
}
