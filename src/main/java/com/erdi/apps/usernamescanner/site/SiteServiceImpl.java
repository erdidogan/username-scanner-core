package com.erdi.apps.usernamescanner.site;

import com.erdi.apps.usernamescanner.exception.CustomHttpClientException;
import com.erdi.apps.usernamescanner.httpclient.HttpClientService;
import com.erdi.apps.usernamescanner.site.model.SiteModel;
import com.erdi.apps.usernamescanner.site.model.SiteResponseModel;
import com.erdi.apps.usernamescanner.source.SourceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final HttpClientService httpClientService;
    private final List<SourceModel> sourceModelList;


    @Override
    public SiteResponseModel getSiteResponse(String username) {
        if (!StringUtils.hasLength(username))
            return new SiteResponseModel(0, username, new LinkedList<>());

        try {
            long startTime = System.currentTimeMillis();
            var list = getSiteModelList(username.replaceAll("[^a-zA-Z0-9-_.]/g", ""));
            long stopTime = System.currentTimeMillis();
            double elapsedTimeInSecond = stopTime - startTime;
            log.warn("Execution completed in {} ms", elapsedTimeInSecond);
            return new SiteResponseModel(list.size(), username, list);
        } catch (ExecutionException | InterruptedException e) {
            throw new CustomHttpClientException(e.getMessage());
        }

    }


    private List<SiteModel> getSiteModelList(String username) throws ExecutionException, InterruptedException {

        var siteModelList = prepareSiteModelListForGivenUsername(username);
        var urlList = prepareSiteUrlList(username);
        var requestList = httpClientService.prepareGetRequestList(urlList);
        var callResultList = httpClientService.executeGet(requestList);

        for (int i = 0; i < siteModelList.size(); i++) {
            SiteModel model = siteModelList.get(i);
            HttpResponse<String> futureResponse = callResultList.get(i).get();
            if (futureResponse.statusCode() == HttpStatus.OK.value() ||
                    futureResponse.statusCode() == HttpStatus.NOT_FOUND.value()) {
                model.setStatus(getStatus(futureResponse, model));
            } else {
                log.warn("User: {}, Site: {}, Status {}", username,
                        sourceModelList.get(i).getSiteName(),
                        futureResponse.statusCode());
                siteModelList.remove(model);
            }

        }
        return siteModelList;


    }

    private int getStatus(HttpResponse<String> response, SiteModel siteModel) {
        if (Objects.isNull(siteModel.getMessage())) {
            return response.statusCode();
        }
        return response.body().contains(siteModel.getMessage()) ? HttpStatus.NOT_FOUND.value() : response.statusCode();
    }

    private List<String> prepareSiteUrlList(String username) {
        var resultList = prepareSiteModelListForGivenUsername(username);
        return resultList.stream().map(SiteModel::getRegisterUrl).collect(Collectors.toList());
    }

    private List<SiteModel> prepareSiteModelListForGivenUsername(String username) {
        List<SiteModel> list = new ArrayList<>();
        final String TARGET = "{}";
        for (SourceModel s : sourceModelList) {
            list.add(SiteModel.builder()
                    .siteName(s.getSiteName().replace(TARGET, username))
                    .siteIconUrl(s.getSiteIconUrl())
                    .registerUrl(s.getSiteRegisterUrl().replace(TARGET, username))
                    .build());

        }
        return list;
    }

}
