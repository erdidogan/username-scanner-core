package com.erdi.apps.usernamescanner.dto.response;

import com.erdi.apps.usernamescanner.dto.SiteModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class SiteResponseModel {
    private long siteCount;
    private String user;
    private List<SiteModel> list;
    private double executionTime;

    @Override
    public String toString() {
        return "SiteResponseModel{" +
                "siteCount=" + siteCount +
                ", user='" + user + '\'' +
                ", executionTime=" + executionTime +
                '}';
    }
}
