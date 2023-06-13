package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;

import java.sql.DatabaseMetaData;

public interface DataBasePersistence {
    ConnectionInfo saveDatabaseMetaData(ConnectionInfo connectionInfo);
}
