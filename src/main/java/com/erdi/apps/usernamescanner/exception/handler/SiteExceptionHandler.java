package com.erdi.apps.usernamescanner.exception.handler;

import com.erdi.apps.usernamescanner.exception.CustomHttpClientException;
import com.erdi.apps.usernamescanner.exception.ErrorResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.sql.Timestamp;

@RestControllerAdvice
public class SiteExceptionHandler {


    @ExceptionHandler(CustomHttpClientException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseModel handleCustomClientException(CustomHttpClientException ex) {
        return new ErrorResponseModel(ex.getMessage(), new Timestamp(System.currentTimeMillis()));

    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseModel handleHttpClientErrorException(CustomHttpClientException ex) {
        return new ErrorResponseModel(ex.getMessage(), new Timestamp(System.currentTimeMillis()));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseModel handleHttpServerException(CustomHttpClientException ex) {
        return new ErrorResponseModel(ex.getMessage(), new Timestamp(System.currentTimeMillis()));
    }


}
