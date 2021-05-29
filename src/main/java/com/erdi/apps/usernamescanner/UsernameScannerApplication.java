package com.erdi.apps.usernamescanner;

import com.erdi.apps.usernamescanner.model.SourceModel;
import com.erdi.apps.usernamescanner.exception.SourceInitializationException;
import com.erdi.apps.usernamescanner.service.impl.SiteServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class UsernameScannerApplication {

	public static final List<SourceModel> sourceModelList;


    public static void main(String[] args) {
        SpringApplication.run(UsernameScannerApplication.class, args);
    }

	static {
		InputStream sourceStream = SiteServiceImpl.class.getResourceAsStream("/static/sources.json");
		if (sourceStream == null)
			throw new SourceInitializationException("Source init error! Can not find source location.");
		SourceModel[] sourceModels = new Gson().fromJson(new BufferedReader(new InputStreamReader(sourceStream)), SourceModel[].class);
		sourceModelList = Arrays.asList(sourceModels);
		log.info("Site init completed. Site Count: " + sourceModelList.size());
	}

}
