package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.DatabaseDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.SchemaDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;
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
import java.util.stream.Collectors;

import static com.dziadkouskaya.dataBaseParsing.utils.Validation.checkSearchRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataBaseParsingServiceImpl implements DataBaseParsingService {
    private final DataBasePersistence dataBasePersistence;
    private final SearchService searchService;

    protected final ConnectionMapper connectionMapper;

    @Override
    public ConnectionDto getConnectionInfoFromPath(String dataBadeConnection, String user, String password) throws DatabaseConnectionException {
        var connectionInfo = createConnectionInfo(dataBadeConnection, user, password);
        connectionInfo = saveConnectionInfo(connectionInfo);
        return connectionMapper.toDto(connectionInfo);
    }

    @Override
    public ConnectionInfo createConnectionInfo(String dataBadeConnection, String user, String password) throws DatabaseConnectionException {
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

    @Override
    public List<DataBase> createDatabases(DatabaseMetaData meta) throws DatabaseConnectionException {
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

    @Override
    public List<DatabaseDto> getExistedDatabases(SearchRequest searchRequest) {
        var existedDatabases = dataBasePersistence.getDatabases();
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var databasesWithSearch = searchService.searchInDatabaseNames(existedDatabases, regex, searchRequest.getSorting());
        return databasesWithSearch.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<SchemaDto> getExistedSchemas(SearchRequest searchRequest) {
        var existedSchemas = dataBasePersistence.getSchemas();
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var limitSortedSchemas = searchService.searchInSchemaNames(existedSchemas, regex, searchRequest.getSorting());
        return limitSortedSchemas.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<DatabaseDto> getDatabasesFromConnection(String connection, String user, String password, SearchRequest searchRequest) throws DatabaseConnectionException {
        var connectionInfo = getConnectionInfoFromPath(connection, user, password);
        var existedDatabases = dataBasePersistence.getDatabasesByHash(connectionInfo.getHash());
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var databasesWithSearch = searchService.searchInDatabaseNames(existedDatabases, regex, searchRequest.getSorting());
        return databasesWithSearch.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<SchemaDto> getSchemasFromConnection(String connection, String user, String password, SearchRequest searchRequest) throws DatabaseConnectionException {
        var connectionInfo = getConnectionInfoFromPath(connection, user, password);
        var schemas = dataBasePersistence.getSchemasByHash(connectionInfo.getHash());
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var limitSortedSchemas = searchService.searchInSchemaNames(schemas, regex, searchRequest.getSorting());
        return limitSortedSchemas.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

}
