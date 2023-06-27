package com.dziadkouskaya.dataBaseParsing.controller.handler;

import com.dziadkouskaya.dataBaseParsing.exception.ApplicationException;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;
import com.dziadkouskaya.dataBaseParsing.exception.EmptyStorageException;
import com.dziadkouskaya.dataBaseParsing.exception.RestErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

import static com.dziadkouskaya.dataBaseParsing.utils.Constants.ACCESS_DENIED_MESSAGE;
import static com.dziadkouskaya.dataBaseParsing.utils.Constants.INNER_SERVER_ERROR_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DatabaseConnectionException.class)
    protected ResponseEntity<RestErrorResponse> handleBindException(DatabaseConnectionException ex, WebRequest request) {
        return handleInternal(ex, INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(EmptyStorageException.class)
    protected ResponseEntity<RestErrorResponse> handleBindException(EmptyStorageException ex, WebRequest request) {
        return handleInternal(ex, INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<RestErrorResponse> handleBindException(ApplicationException ex, WebRequest request) {
        return handleInternal(ex, INTERNAL_SERVER_ERROR, INNER_SERVER_ERROR_MESSAGE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return handleInternal(ex, FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }


    private ResponseEntity<RestErrorResponse> handleInternal(Exception ex, HttpStatus status, String error) {
        log.error(error, ex);
        return new ResponseEntity<>(new RestErrorResponse(error), new HttpHeaders(), status);
    }
}
