package com.erdi.apps.usernamescanner.service;

import com.erdi.apps.usernamescanner.dto.SiteResponseModel;

public interface SiteService {

    SiteResponseModel findAll(String username);


}
