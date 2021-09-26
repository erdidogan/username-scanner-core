package com.erdi.apps.usernamescanner.site.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Builder
@ToString
public class SiteModel {

    private final String siteName;
    private int status;
    private final String registerUrl;
    private final String siteIconUrl;

    @JsonIgnore
    private final String message;

    public void setStatus(int status) {
        this.status = status;
    }
}
