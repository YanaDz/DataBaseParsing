package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;

import java.sql.DatabaseMetaData;

public interface DataBaseParsingService {
    ConnectionDto getConnectionInfoFromPath(String dataBadeConnection, String user, String password) throws DatabaseConnectionException;

    ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo);
}
