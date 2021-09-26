package com.erdi.apps.usernamescanner.controller;

import com.erdi.apps.usernamescanner.site.SiteService;
import com.erdi.apps.usernamescanner.site.model.SiteResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("api/v1/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("all")
    public SiteResponseModel discoverUsers(@RequestParam String username) {
        return siteService.getSiteResponse(username);
    }
}
