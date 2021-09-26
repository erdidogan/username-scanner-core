package com.erdi.apps.usernamescanner.source;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class SourceConfig {

    @Bean
    public List<SourceModel> sourceModelList() throws IOException {
        Resource resource = new ClassPathResource("sources.json");
        InputStream sourceStream = resource.getInputStream();
        try (sourceStream) {
            SourceModel[] sourceModels = new Gson().fromJson(new BufferedReader(new InputStreamReader(sourceStream)), SourceModel[].class);
            log.info("Site init completed. Site Count: " + sourceModels.length);
            return Arrays.asList(Arrays.copyOf(sourceModels, sourceModels.length));
        }
    }
}

