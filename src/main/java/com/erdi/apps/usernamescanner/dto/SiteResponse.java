package com.erdi.apps.usernamescanner.dto;

import com.erdi.apps.usernamescanner.model.SiteModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SiteResponse {
    private long c;
    private String u;
    private List<SiteModel> l;
    private double et;

    public SiteResponse(String u, List<SiteModel> l, double et) {
        this.u = u;
        this.l = l;
        this.c = l.size();
        this.et = et;
    }

    @Override
    public String toString() {
        return "{" +
                "c=" + c +
                ", u='" + u + '\'' +
                ", et=" + et +
                '}';
    }
}
