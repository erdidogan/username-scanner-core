package com.erdi.apps.usernamescanner.site.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SiteResponseModel {
    private long count;
    private String user;
    private List<SiteModel> list;
}
