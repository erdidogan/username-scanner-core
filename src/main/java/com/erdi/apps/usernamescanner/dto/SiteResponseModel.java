package com.erdi.apps.usernamescanner.dto;

import com.erdi.apps.usernamescanner.model.SiteModel;
import lombok.*;

import java.util.List;

@Data
@ToString
public class SiteResponseModel {
    private long count;
    private String user;
    private List<SiteModel> list;

    public SiteResponseModel(String user, List<SiteModel> list) {
        this.user = user;
        this.list = list;
        this.count = list.size();
    }


}
