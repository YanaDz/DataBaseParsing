package com.dziadkouskaya.dataBaseParsing.controller;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionRequest;
import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.DatabaseDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.SchemaDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;
import com.dziadkouskaya.dataBaseParsing.service.DataBaseParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public ConnectionDto createCatalogByConnection(@RequestBody ConnectionRequest request) throws DatabaseConnectionException {
        return dataBaseParsingService.getConnectionInfoFromPath(request.getConnection(), request.getUser(), request.getPassword());
    }

    @PostMapping(value = "/connection-databases")
    @ResponseStatus(HttpStatus.OK)
    public List<DatabaseDto> getDatabasesFromConnection(@RequestBody ConnectionRequest request, SearchRequest searchRequest) throws DatabaseConnectionException {
        return dataBaseParsingService.getDatabasesFromConnection(request.getConnection(), request.getUser(), request.getPassword(), searchRequest);
    }

    @PostMapping(value = "/connection-schemas")
    @ResponseStatus(HttpStatus.OK)
    public List<SchemaDto> getSchemasFromConnection(@RequestBody ConnectionRequest request, SearchRequest searchRequest) throws DatabaseConnectionException {
        return dataBaseParsingService.getSchemasFromConnection(request.getConnection(), request.getUser(), request.getPassword(), searchRequest);
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
