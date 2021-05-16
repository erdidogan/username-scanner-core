package com.erdi.apps.usernamescanner.source;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Source {

    private String siteName;
    private String siteUrl;
    private String siteIconUrl;
    private String siteRegisterUrl;
    private String message;
    private String contentType;
    private String body;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return getSiteName().equals(source.getSiteName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSiteName());
    }
}
