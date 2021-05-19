package com.erdi.apps.usernamescanner.controller;

import com.erdi.apps.usernamescanner.dto.SiteResponseModel;
import com.erdi.apps.usernamescanner.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("find/all")
    public Mono<SiteResponseModel> discoverUsers(@RequestParam @NotNull @Length(min = 4) String username) {
        return Mono.just(siteService.findAll(username.replaceAll("[^a-zA-Z0-9-_.]/g", "")));

    }

}
