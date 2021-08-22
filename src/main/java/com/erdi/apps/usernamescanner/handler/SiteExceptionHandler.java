package com.erdi.apps.usernamescanner.handler;

import com.erdi.apps.usernamescanner.dto.ErrorResponseModel;
import com.erdi.apps.usernamescanner.exception.CustomHttpClientException;
import com.erdi.apps.usernamescanner.exception.SourceInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

@RestControllerAdvice
public class SiteExceptionHandler {


    @ExceptionHandler(SourceInitializationException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponseModel> sourceInitializationException(SourceInitializationException ex) {
        ErrorResponseModel error = new ErrorResponseModel(ex.getMessage(), new Timestamp(System.currentTimeMillis()));
        return Mono.just(error);
    }

    @ExceptionHandler(CustomHttpClientException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponseModel> customHttpClientException(CustomHttpClientException ex) {
        ErrorResponseModel error = new ErrorResponseModel(ex.getMessage(), new Timestamp(System.currentTimeMillis()));
        return Mono.just(error);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponseModel> exception(CustomHttpClientException ex) {
        ErrorResponseModel error = new ErrorResponseModel(ex.getMessage(), new Timestamp(System.currentTimeMillis()));
        return Mono.just(error);
    }


}
