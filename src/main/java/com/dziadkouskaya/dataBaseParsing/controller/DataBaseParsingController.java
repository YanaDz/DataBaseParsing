package com.dziadkouskaya.dataBaseParsing.controller;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.service.DataBaseParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DatabaseMetaData;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = DataBaseParsingController.PATH)
public class DataBaseParsingController {
    public static final String PATH = "/parsing";

    private final DataBaseParsingService dataBaseParsingService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ConnectionInfo getCatalogByConnection(String connection, String user, String password) {
        return dataBaseParsingService.getConnectionInfoFromPath(connection, user, password);
    }
}
