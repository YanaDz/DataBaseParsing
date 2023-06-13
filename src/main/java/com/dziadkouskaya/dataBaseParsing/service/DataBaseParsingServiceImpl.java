package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;
import com.dziadkouskaya.dataBaseParsing.persistence.DataBasePersistence;
import com.dziadkouskaya.dataBaseParsing.service.mapper.ConnectionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataBaseParsingServiceImpl implements DataBaseParsingService {
    private final DataBasePersistence dataBasePersistence;

    protected final ConnectionMapper connectionMapper;

    @Override
    public ConnectionDto getConnectionInfoFromPath(String dataBadeConnection, String user, String password) throws DatabaseConnectionException {
        var connectionInfo = createConnectionInfo(dataBadeConnection, user, password);
        connectionInfo = saveConnectionInfo(connectionInfo);
        return connectionMapper.toDto(connectionInfo);
    }

    private ConnectionInfo createConnectionInfo(String dataBadeConnection, String user, String password) throws DatabaseConnectionException {
        ConnectionInfo connectionInfo = null;
        try (Connection connection = DriverManager.getConnection(dataBadeConnection, user, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            connectionInfo = connectionMapper.toConnectionInfo(dataBadeConnection, metaData);
            var databases = createDatabases(metaData);
            connectionInfo.getDatabases().addAll(databases);
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
        return connectionInfo;
    }

    private List<DataBase> createDatabases(DatabaseMetaData meta) throws DatabaseConnectionException {
        var databases = new ArrayList<DataBase>();
        try (ResultSet resultSet = meta.getCatalogs()) {
            while (resultSet.next()) {
                var database = connectionMapper.toDatabase(resultSet);
                var schemaResultSet = meta.getSchemas(database.getDatabaseName(), null);

                while (schemaResultSet.next()) {
                    var schema = connectionMapper.toSchema(schemaResultSet);
                    var tableResultSet = meta.getTables(null, schema.getSchemaName(), null, new String[]{"TABLE"});

                    while (tableResultSet.next()) {
                        var table = connectionMapper.toTable(tableResultSet);
                        ResultSet columnResultSet = meta.getColumns(null, null, table.getTableName(), null);

                        while (columnResultSet.next()) {
                            var column = connectionMapper.toColumn(columnResultSet);
                            table.getColumns().add(column);
                        }
                        schema.getTables().add(table);
                    }
                    database.getSchemas().add(schema);
                }
                databases.add(database);
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
        return databases;
    }

    @Override
    public ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo) {
        return dataBasePersistence.saveConnectionInfo(connectionInfo);
    }

}
