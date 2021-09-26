package com.erdi.apps.usernamescanner.httpclient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HttpClientService {

    List<CompletableFuture<HttpResponse<String>>> executeGet(List<HttpRequest> requestList);

    List<HttpRequest> prepareGetRequestList(List<String> urlList);
}
