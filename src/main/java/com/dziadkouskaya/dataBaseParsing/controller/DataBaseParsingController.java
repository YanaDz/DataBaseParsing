package com.dziadkouskaya.dataBaseParsing.controller;

import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.DatabaseDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.SchemaDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;
import com.dziadkouskaya.dataBaseParsing.service.DataBaseParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = DataBaseParsingController.PATH)
public class DataBaseParsingController {
    public static final String PATH = "/parsing";

    private final DataBaseParsingService dataBaseParsingService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ConnectionDto createCatalogByConnection(String connection, String user, String password) throws DatabaseConnectionException {
        return dataBaseParsingService.getConnectionInfoFromPath(connection, user, password);
    }

    @GetMapping(value = "/connection-databases")
    @ResponseStatus(HttpStatus.OK)
    public List<DatabaseDto> getDatabasesFromConnection(String connection, String user, String password, SearchRequest searchRequest) throws DatabaseConnectionException {
        return dataBaseParsingService.getDatabasesFromConnection(connection, user, password, searchRequest);
    }

    @GetMapping(value = "/connection-schemas")
    @ResponseStatus(HttpStatus.OK)
    public List<SchemaDto> getSchemasFromConnection(String connection, String user, String password, SearchRequest searchRequest) throws DatabaseConnectionException {
        return dataBaseParsingService.getSchemasFromConnection(connection, user, password, searchRequest);
    }

    @GetMapping(value = "/databases")
    @ResponseStatus(HttpStatus.OK)
    public List<DatabaseDto> getAllExistedDatabases(SearchRequest searchRequest) {
        return dataBaseParsingService.getExistedDatabases(searchRequest);
    }

    @GetMapping(value = "/schemas")
    @ResponseStatus(HttpStatus.OK)
    public List<SchemaDto> getAllExistedSchemas(SearchRequest searchRequest) {
        return dataBaseParsingService.getExistedSchemas(searchRequest);
    }
}
