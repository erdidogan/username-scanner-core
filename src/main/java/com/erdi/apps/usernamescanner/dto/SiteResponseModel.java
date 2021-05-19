package com.erdi.apps.usernamescanner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class SiteResponseModel {
    private long siteCount;
    private String user;
    private List<SiteListModel> list;
    private double executionTime;
}
