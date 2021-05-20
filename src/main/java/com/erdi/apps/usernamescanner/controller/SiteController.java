package com.erdi.apps.usernamescanner.controller;

import com.erdi.apps.usernamescanner.dto.SiteModel;
import com.erdi.apps.usernamescanner.dto.response.SiteResponseModel;
import com.erdi.apps.usernamescanner.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("find/all")
    public Mono<SiteResponseModel> discoverUsers(@RequestParam String username) {
        if(StringUtils.hasLength(username) && username.length()>3 && username.length()< 10)
            return Mono.just(siteService.findAll(username.replaceAll("[^a-zA-Z0-9-_.]/g", "")));
        else
            return Mono.just(new SiteResponseModel(0,"",new LinkedList<>(),0));

    }

}
