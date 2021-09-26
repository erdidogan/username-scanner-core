package com.erdi.apps.usernamescanner.source;

import lombok.Getter;
import lombok.Setter;

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
