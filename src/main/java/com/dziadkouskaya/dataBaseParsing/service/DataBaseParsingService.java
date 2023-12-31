package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.ConnectionRequest;
import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.DatabaseDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.SchemaDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;

import java.util.List;

public interface DataBaseParsingService {
    ConnectionDto getConnectionInfoFromPath(String dataBadeConnection, String user, String password) throws DatabaseConnectionException;

    ConnectionInfo createConnectionInfo(String dataBadeConnection, String user, String password) throws DatabaseConnectionException;
    ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo);

    List<DatabaseDto> getExistedDatabases(SearchRequest searchRequest);

    List<SchemaDto> getExistedSchemas(SearchRequest searchRequest);

    List<DatabaseDto> getDatabasesFromConnection(String connection, String user, String password, SearchRequest searchRequest) throws DatabaseConnectionException;


    List<SchemaDto> getSchemasFromConnection(String connection, String user, String password, SearchRequest searchRequest) throws DatabaseConnectionException;


    List<DatabaseDto> updateToUpperCaseExistedDatabases();

    List<DatabaseDto> updateToUpperCaseFromConnection(String connection, String user, String password) throws DatabaseConnectionException;
}
