package com.erdi.apps.usernamescanner.httpclient;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class HttpClientServiceServiceImpl implements HttpClientService {

    private static HttpClient client;


    public HttpClientServiceServiceImpl() {
        client = initHttpClient();
    }

    private HttpClient initHttpClient() {
        return client = HttpClient.newBuilder()
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .version(java.net.http.HttpClient.Version.HTTP_2)
                .executor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
                .build();
    }

    @Override
    public List<CompletableFuture<HttpResponse<String>>> executeGet(List<HttpRequest> requestList) {
        client = initHttpClient();
        return requestList.stream()
                .map(httpRequest -> client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                        .thenApply(stringHttpResponse -> stringHttpResponse))
                .collect(Collectors.toList());
    }

    @Override
    public List<HttpRequest> prepareGetRequestList(List<String> urlList) {
        return urlList.stream().map(url -> HttpRequest.newBuilder(URI.create(url))
                .GET()
                .setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0")
                .timeout(Duration.ofMillis(2500))
                .build()).collect(Collectors.toList());
    }
}
