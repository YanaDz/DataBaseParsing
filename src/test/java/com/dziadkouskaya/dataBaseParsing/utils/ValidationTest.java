package com.dziadkouskaya.dataBaseParsing.utils;

import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.Sorting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ValidationTest {

    static Stream<Arguments> argForRequestValidation() {
        return Stream.of(
                arguments("initial", "initial"),
                arguments("  ", "  "),
                arguments("", ""),
                arguments(null, "")
        );
    }
    @ParameterizedTest
    @MethodSource("argForRequestValidation")
    void checkSearchRequest(String initial, String expected) {
        var searchRequest = SearchRequest.builder().search(initial).build();
        var result = Validation.checkSearchRequest(searchRequest);
        assertEquals(expected, result.getSearch());
    }
}