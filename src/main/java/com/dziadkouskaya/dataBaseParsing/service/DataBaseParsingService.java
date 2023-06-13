package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;

import java.sql.DatabaseMetaData;
import java.util.List;

public interface DataBaseParsingService {
    ConnectionDto getConnectionInfoFromPath(String dataBadeConnection, String user, String password) throws DatabaseConnectionException;

    public ConnectionInfo createConnectionInfo(String dataBadeConnection, String user, String password) throws DatabaseConnectionException;
    public List<DataBase> createDatabases(DatabaseMetaData meta) throws DatabaseConnectionException;
    ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo);
}
