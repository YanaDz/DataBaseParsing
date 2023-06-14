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
        log.info("Get information from connection {}.", dataBadeConnection);
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
            log.info("Created {} databases.", databases.size());
        } catch (SQLException e) {
            log.error("Exception during taking info from connection: {}", dataBadeConnection);
            throw new DatabaseConnectionException(e);
        }
        return connectionInfo;
    }


    private List<DataBase> createDatabases(DatabaseMetaData meta) throws DatabaseConnectionException {
        var databases = new ArrayList<DataBase>();
        try (ResultSet resultSet = meta.getCatalogs()) {
            while (resultSet.next()) {
                var database = connectionMapper.toDatabase(resultSet);
                log.info("Created database with name {}.", database.getDatabaseName());
                var schemaResultSet = meta.getSchemas(database.getDatabaseName(), null);

                while (schemaResultSet.next()) {
                    var schema = connectionMapper.toSchema(schemaResultSet);
                    log.info("Created schema with name {}.", schema.getSchemaName());
                    var tableResultSet = meta.getTables(null, schema.getSchemaName(), null, new String[]{"TABLE"});

                    while (tableResultSet.next()) {
                        var table = connectionMapper.toTable(tableResultSet);
                        log.info("Created table with name {}.", table.getTableName());
                        ResultSet columnResultSet = meta.getColumns(null, null, table.getTableName(), null);

                        while (columnResultSet.next()) {
                            var column = connectionMapper.toColumn(columnResultSet);
                            table.getColumns().add(column);
                        }
                        log.info("Created {} columns in database {}.", table.getColumns().size(), table.getTableName());
                        schema.getTables().add(table);
                    }
                    database.getSchemas().add(schema);
                }
                databases.add(database);
            }
        } catch (SQLException e) {
            log.error("Exception during transfer DatabaseMetaData to inner storage.");
            throw new DatabaseConnectionException(e);
        }
        return databases;
    }

    @Override
    public ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo) {
        var connection = dataBasePersistence.saveConnectionInfo(connectionInfo);
        log.info("Connection from {} and hash {} was saved to storage.", connectionInfo.getConnectionPath(),
            connectionInfo.getConnectionHash());
        return connection;
    }

    @Override
    public List<DatabaseDto> getExistedDatabases(SearchRequest searchRequest) {
        log.info("Start getting info about existed databases with params: search field {}, sort {}.",
            searchRequest.getSearch(), searchRequest.getSorting());
        var existedDatabases = dataBasePersistence.getDatabases();
        log.info("Number of existed databases in storage is {}.", existedDatabases.size());
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var filteringDatabases = searchService.searchInDatabaseNames(existedDatabases, regex, searchRequest.getSorting());
        log.info("Number of databases after the filtering is {}", filteringDatabases.size());
        return filteringDatabases.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<SchemaDto> getExistedSchemas(SearchRequest searchRequest) {
        log.info("Start getting info about existed schemas with params: search field {}, sort {}.",
            searchRequest.getSearch(), searchRequest.getSorting());
        var existedSchemas = dataBasePersistence.getSchemas();
        log.info("Number of existed schemas in storage is {}.", existedSchemas.size());
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var limitSortedSchemas = searchService.searchInSchemaNames(existedSchemas, regex, searchRequest.getSorting());
        log.info("Number of schemas after the filtering is {}", limitSortedSchemas.size());
        return limitSortedSchemas.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<DatabaseDto> getDatabasesFromConnection(String connection, String user, String password, SearchRequest searchRequest)
        throws DatabaseConnectionException {
        log.info("Start getting databases info from connection {}.", connection);
        var connectionInfo = getConnectionInfoFromPath(connection, user, password);
        var existedDatabases = dataBasePersistence.getDatabasesByHash(connectionInfo.getHash());
        log.info("Number of all database from connection is {}.", existedDatabases.size());
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var databasesWithSearch = searchService.searchInDatabaseNames(existedDatabases, regex, searchRequest.getSorting());
        log.info("Number of databases after the filtering by search field {} is {}",searchRequest.getSearch(),  databasesWithSearch.size());
        return databasesWithSearch.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<SchemaDto> getSchemasFromConnection(String connection, String user, String password, SearchRequest searchRequest)
        throws DatabaseConnectionException {
        log.info("Start getting schemas info from connection {}.", connection);
        var connectionInfo = getConnectionInfoFromPath(connection, user, password);
        var schemas = dataBasePersistence.getSchemasByHash(connectionInfo.getHash());
        log.info("Number of all schemas from connection is {}.", schemas.size());
        checkSearchRequest(searchRequest);
        var regex = searchService.createRegex(searchRequest);
        var limitSortedSchemas = searchService.searchInSchemaNames(schemas, regex, searchRequest.getSorting());
        log.info("Number of schemas after the filtering by search field {} is {}",searchRequest.getSearch(),
            limitSortedSchemas.size());
        return limitSortedSchemas.stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<DatabaseDto> updateToUpperCaseExistedDatabases() {
        log.info("Start updated entities from storage to upper case");
        var existedConnections = dataBasePersistence.getConnections();
        log.info("Number of existed connections is {}.", existedConnections.size());
        existedConnections.values()
            .forEach(connectionInfo -> {
                connectionInfo.getDatabases().forEach(DataBase::toUpperCase);
                dataBasePersistence.saveConnectionInfo(connectionInfo);
            });
        log.info("Finished updating entities from storage to upper case");
        return dataBasePersistence.getDatabases().stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<DatabaseDto> updateToUpperCaseFromConnection(String connection, String user, String password)
        throws DatabaseConnectionException {
        log.info("Start updated entities from connection {} to upper case.", connection);
        var connectionInfo = createConnectionInfo(connection, user, password);
        connectionInfo.getDatabases().forEach(DataBase::toUpperCase);
        connectionInfo = saveConnectionInfo(connectionInfo);
        log.info("Updated {} database to upper case.", connectionInfo.getDatabases().size());
        return connectionInfo.getDatabases().stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

}
