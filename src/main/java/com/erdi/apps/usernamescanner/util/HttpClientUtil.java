package com.erdi.apps.usernamescanner.util;

import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public final class HttpClientUtil {

    private static final String USER_AGENT = "Mozilla/5.0 Firefox/26.0";


    public static HttpRequest buildGetRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .GET()
                .setHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .build();

    }

    public static HttpRequest buildPostRequest(String url, String contentType, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .header("Content-Type", contentType)
                .header("Cookie", "xsrf_token=PlEcin8s5H600toD4Swngg")
                .header("x-csrftoken", "oyMLA34p6Q6qZkfXMqGmsK62Jouv5Xpj")
                .header("Client-Id", "kimne78kx3ncx6brgo4mv6wki5h1ko")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    public static List<CompletableFuture<HttpResponse<String>>> concurrentCall(List<HttpRequest> httpRequestList) {

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .executor(Executors.newCachedThreadPool())
                .build();

        return httpRequestList.stream()
                .map(httpRequest -> httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                        .thenApply(stringHttpResponse -> stringHttpResponse))
                .collect(Collectors.toList());
    }
}
