package com.erdi.apps.usernamescanner.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SiteModel {

    private final String siteName;
    private final int status;
    private final String registerUrl;
    private final String siteIconUrl;

}
