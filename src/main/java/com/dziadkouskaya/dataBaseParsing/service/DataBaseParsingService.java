package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;

import java.sql.DatabaseMetaData;

public interface DataBaseParsingService {
    ConnectionInfo getConnectionInfoFromPath(String dataBadeConnection, String user, String password);

    DatabaseMetaData saveDatabaseMeta(String dataBadeConnection, String user, String password, DatabaseMetaData meta);
}
