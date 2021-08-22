package com.erdi.apps.usernamescanner.controller;

import com.erdi.apps.usernamescanner.dto.SiteResponseModel;
import com.erdi.apps.usernamescanner.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("find/all")
    public Mono<SiteResponseModel> discoverUsers(@RequestParam String username) {
        if (StringUtils.hasLength(username))
            return Mono.just(siteService.findAll(username));
        else
            return Mono.just(new SiteResponseModel("", new LinkedList<>()));
    }
}
