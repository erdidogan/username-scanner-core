package com.erdi.apps.usernamescanner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class SiteResponseModel {
    private long siteCount;
    private List<SiteListResponseModel> list;
    private double executionTime;
}
