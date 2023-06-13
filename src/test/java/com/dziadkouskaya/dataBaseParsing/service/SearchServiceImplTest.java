package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.DatabaseSchema;
import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.Sorting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SearchServiceImplTest {
    @InjectMocks
    private SearchServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    static Stream<Arguments> argForRegex() {
        return Stream.of(
                arguments("1", ".*1.*"),
                arguments("asd", ".*asd.*"),
                arguments(" ", ".* .*")
        );
    }

    @ParameterizedTest
    @MethodSource("argForRegex")
    void createRegex(String intial, String expected) {
        var searchRequest = SearchRequest.builder().search(intial).build();
        var result = service.createRegex(searchRequest);
        assertEquals(expected, result);
    }

    static Stream<Arguments> argForSearchInDb() {
        return Stream.of(
                arguments("", null, 4, "database12"),
                arguments("database", null, 4, "database12"),
                arguments("2", null, 3, "database12"),
                arguments("4", null, 2, "database4"),
                arguments("4", Sorting.ASC, 2, "database4"),
                arguments("4", Sorting.DESC, 2, "database42"),
                arguments("stjsrj", Sorting.DESC, 0, null)
        );
    }

    @ParameterizedTest
    @MethodSource("argForSearchInDb")
    void searchInDatabaseNames(String search, Sorting sort, int expectedSize, String db1Element) {
        var databases = List.of(
                DataBase.builder().databaseName("database12").build(),
                DataBase.builder().databaseName("database4").build(),
                DataBase.builder().databaseName("database2").build(),
                DataBase.builder().databaseName("database42").build()
        );
        var regex = service.createRegex(SearchRequest.builder().search(search).build());
        var result = service.searchInDatabaseNames(databases, regex, sort);
        assertEquals(expectedSize, result.size());
        if (expectedSize > 0) {
            assertEquals(db1Element, result.get(0).getDatabaseName());
        }
    }

    static Stream<Arguments> argForSearchInSchemas() {
        return Stream.of(
                arguments("", null, 4, "public"),
                arguments("public", null, 2, "public"),
                arguments("gaergehr", null, 0, null),
                arguments("_", null, 3, "path_connect"),
                arguments("_", Sorting.ASC, 3, "initial_persons"),
                arguments("_", Sorting.DESC, 3, "public_entity"),
                arguments("dhfboabpir", Sorting.DESC, 0, null)
        );
    }

    @ParameterizedTest
    @MethodSource("argForSearchInSchemas")
    void searchInSchemaNames(String search, Sorting sort, int expectedSize, String schemas1Element) {
        var schemas = List.of(
                DatabaseSchema.builder().schemaName("public").build(),
                DatabaseSchema.builder().schemaName("path_connect").build(),
                DatabaseSchema.builder().schemaName("public_entity").build(),
                DatabaseSchema.builder().schemaName("initial_persons").build()
        );
        var regex = service.createRegex(SearchRequest.builder().search(search).build());
        var result = service.searchInSchemaNames(schemas, regex, sort);
        assertEquals(expectedSize, result.size());
        if (expectedSize > 0) {
            assertEquals(schemas1Element, result.get(0).getSchemaName());
        }
    }
}