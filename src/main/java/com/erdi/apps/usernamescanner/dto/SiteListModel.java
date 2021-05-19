package com.erdi.apps.usernamescanner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SiteListModel {

    private final String siteName;
    private String  status;
    private final String registerUrl;
    private final String siteIconUrl;

}
