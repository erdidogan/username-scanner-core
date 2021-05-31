package com.erdi.apps.usernamescanner.service;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HttpClientService {

    List<CompletableFuture<HttpResponse<String>>> makeConcurrentGetCall(String username);

}
