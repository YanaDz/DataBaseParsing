package com.dziadkouskaya.dataBaseParsing.exception;

import lombok.Data;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.singletonList;

@Data
public class RestErrorResponse {
    private Instant timestamp = Instant.now();
    private List<String> errors;

    public RestErrorResponse(List<String> errors) {
        this.errors = errors;
    }

    public RestErrorResponse(String error) {
        errors = singletonList(error);
    }
}
