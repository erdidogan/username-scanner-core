package com.erdi.apps.usernamescanner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SiteListResponseModel {

    private final String siteName;
    private int statusCode;
    private final String registerUrl;
    private final String siteIconUrl;

}
