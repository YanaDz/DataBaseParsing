package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.DatabaseSchema;

import java.util.List;

public interface DataBasePersistence {
    ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo);

    List<DataBase> getDatabases();

    List<DatabaseSchema> getSchemas();

    List<DataBase> getDatabasesByHash(Integer hash);

    List<DatabaseSchema> getSchemasByHash(Integer hash);
}
