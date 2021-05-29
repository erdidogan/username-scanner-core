package com.erdi.apps.usernamescanner.service.impl;

import com.erdi.apps.usernamescanner.UsernameScannerApplication;
import com.erdi.apps.usernamescanner.model.SourceModel;
import com.erdi.apps.usernamescanner.service.HttpClientService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class HttpClientServiceImpl implements HttpClientService {

    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0";

    @Override
    public List<CompletableFuture<HttpResponse<String>>> makeConcurrentGetCall(String username) {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .executor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
                .build();

        var httpRequestList = prepareAndBuildHttpRequest(username);

        return httpRequestList.stream()
                .map(httpRequest -> httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                        .thenApply(stringHttpResponse -> stringHttpResponse))
                .collect(Collectors.toList());

    }


    private HttpRequest buildGetRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .GET()
                .setHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .build();
    }


    private HttpRequest buildPostRequest(String url, String contentType, String body) {
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


    private List<HttpRequest> prepareAndBuildHttpRequest(String username) {
        var sourceModelList = UsernameScannerApplication.sourceModelList;
        List<HttpRequest> requestList = new ArrayList<>();
        final String TARGET = "{}";
        for (SourceModel s : sourceModelList) {
            String siteUrl = s.getSiteUrl().replace(TARGET, username);
            if (s.getBody() != null && s.getContentType() != null) {
                String body = s.getBody().replace("####", username);
                requestList.add(buildPostRequest(siteUrl, s.getContentType(), body));
            } else {
                requestList.add(buildGetRequest(siteUrl));
            }
        }
        return requestList;
    }

}
