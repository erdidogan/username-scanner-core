package com.erdi.apps.usernamescanner.handler;

import com.erdi.apps.usernamescanner.dto.response.ErrorResponse;
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
    public Mono<ErrorResponse> sourceInitializationException(SourceInitializationException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), new Timestamp(System.currentTimeMillis()));
        return Mono.just(error);
    }

    @ExceptionHandler(CustomHttpClientException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> customHttpClientException(CustomHttpClientException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), new Timestamp(System.currentTimeMillis()));
        return Mono.just(error);
    }


}
