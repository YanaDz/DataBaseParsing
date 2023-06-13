package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.persistence.DataBasePersistence;
import com.dziadkouskaya.dataBaseParsing.service.mapper.ConnectionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataBaseParsingServiceImpl implements DataBaseParsingService {
    private final DataBasePersistence dataBasePersistence;

    protected final ConnectionMapper connectionMapper;
    @Override
    public ConnectionInfo getConnectionInfoFromPath(String dataBadeConnection, String user, String password) {
        ConnectionInfo connectionInfo = null;
        try {
            Connection connection = DriverManager.getConnection(dataBadeConnection, user, password);
            DatabaseMetaData metaData = connection.getMetaData();
            connectionInfo = createInitialConnectionInfo(dataBadeConnection, metaData);
            System.out.println(connectionInfo);

            var databases = createDatabases(metaData);

            ResultSet resultSet = metaData.getSchemas();
            int schemaCount = 0;
            while (resultSet.next()) {
                schemaCount++;
            }

            System.out.println("Number of database schemas: " + schemaCount);
//
//            ResultSet resultSet2 = metaData.getCatalogs();
//            while (resultSet2.next()) {
//                String databaseName = resultSet2.getString("TABLE_CAT");
//                System.out.println("Database: " + databaseName);
//            }

            resultSet.close();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ConnectionInfo createInitialConnectionInfo(String path, DatabaseMetaData meta) throws SQLException {
        return connectionMapper.toConnectionInfo(path, meta);
    }

    private List<DataBase> createDatabases(DatabaseMetaData meta) throws SQLException {
        ResultSet resultSet = meta.getCatalogs();
        var databases = new ArrayList<DataBase>();
        while (resultSet.next()) {
            var database = connectionMapper.toDatabase(resultSet);
            var schemaResultSet = meta.getSchemas(database.getDatabaseName(), null);

            while (schemaResultSet.next()) {
                var schema = connectionMapper.toSchema(schemaResultSet);
                System.out.println("Schema: " + schema);
                var tableResultSet = meta.getTables(null, schema.getSchemaName(), null, new String[] {"TABLE"});

                while (tableResultSet.next()) {
                    var table = connectionMapper.toTable(tableResultSet);
                    System.out.println("Table: " + table);

                    ResultSet columnResultSet = meta.getColumns(null, null, table.getTableName(), null);

                    while (columnResultSet.next()) {
                        var column = connectionMapper.toColumn(columnResultSet);
                        table.getColumns().add(column);
                    }

                    columnResultSet.close();
                    schema.getTables().add(table);

                }
                tableResultSet.close();
                database.getSchemas().add(schema);
            }
            schemaResultSet.close();

            databases.add(database);

        }
        System.out.println("DATABASES " + databases);
        return databases;
    }



    @Override
    public DatabaseMetaData saveDatabaseMeta(String dataBadeConnection, String user, String password, DatabaseMetaData meta) {
        return null;
    }

}
