package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.*;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.DatabaseDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.SchemaDto;
import com.dziadkouskaya.dataBaseParsing.persistence.DataBasePersistence;
import com.dziadkouskaya.dataBaseParsing.service.mapper.ConnectionMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataBaseParsingServiceImplTest {
    @Mock
    private ConnectionMapper connectionMapper;

    @Mock
    private DataBasePersistence dataBasePersistence;

    @Mock
    private SearchServiceImpl searchService;

    @InjectMocks
    private DataBaseParsingServiceImpl service;

    @Mock
    private Connection mockConnection = mock(Connection.class);

    @Mock
    private DatabaseMetaData metaData = mock(DatabaseMetaData.class);

    @Mock
    private ResultSet catalogsResultSet;

    @Mock
    private ResultSet schemasResultSet;

    @Mock
    private ResultSet tablesResultSet;

    @Mock
    private ResultSet columnsResultSet;

    private MockedStatic<DriverManager> staticMock;

    private final String path = "jdbc:postgresql://localhost:5432/database";
    private final String user = "user";
    private final String password = "password";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        staticMock = Mockito.mockStatic(DriverManager.class);
        staticMock.when(() -> DriverManager.getConnection(path, user, password)).thenReturn(mockConnection);
    }

    @AfterEach
    void clear() {
        staticMock.close();
    }

    @Test
    void saveConnectionInfo() {
        var connectionInfo = ConnectionInfo.builder().connectionHash(Integer.MAX_VALUE).build();
        when(dataBasePersistence.saveConnectionInfo(any())).thenReturn(connectionInfo);
        var result = service.saveConnectionInfo(connectionInfo);
        assertEquals(Integer.MAX_VALUE, result.getConnectionHash());
        verify(dataBasePersistence).saveConnectionInfo(connectionInfo);
    }
    @Test
    void testGetExistedDatabases() {
        var searchRequest = SearchRequest.builder().search("database").build();
        var databases = List.of(
                DataBase.builder().databaseName("database12").build(),
                DataBase.builder().databaseName("database4").build(),
                DataBase.builder().databaseName("database2").build(),
                DataBase.builder().databaseName("database42").build()
        );
        when(dataBasePersistence.getDatabases()).thenReturn(databases);
        when(searchService.createRegex(searchRequest)).thenReturn(searchRequest.getSearch());
        when(searchService.searchInDatabaseNames(databases, searchRequest.getSearch(), searchRequest.getSorting()))
                .thenReturn(databases);
        when(connectionMapper.toDto(any(DataBase.class))).thenReturn(DatabaseDto.builder().build());

        var result = service.getExistedDatabases(searchRequest);
        assertEquals(databases.size(), result.size());

        verify(dataBasePersistence).getDatabases();
        verify(searchService).createRegex(any());
        verify(searchService).searchInDatabaseNames(databases, searchRequest.getSearch(), searchRequest.getSorting());
        verify(connectionMapper, times(databases.size())).toDto(any(DataBase.class));
    }

    @Test
    void testGetExistedSchemas() {
        var searchRequest = SearchRequest.builder().search("database").build();
        var schemas = List.of(
                DatabaseSchema.builder().schemaName("schem1").build(),
                DatabaseSchema.builder().schemaName("schem100").build()
        );
        when(dataBasePersistence.getSchemas()).thenReturn(schemas);
        when(searchService.createRegex(searchRequest)).thenReturn(searchRequest.getSearch());
        when(searchService.searchInSchemaNames(schemas, searchRequest.getSearch(), searchRequest.getSorting()))
                .thenReturn(schemas);
        when(connectionMapper.toDto(any(DatabaseSchema.class))).thenReturn(SchemaDto.builder().build());

        var result = service.getExistedSchemas(searchRequest);
        assertEquals(schemas.size(), result.size());

        verify(dataBasePersistence).getSchemas();
        verify(searchService).createRegex(any());
        verify(searchService).searchInSchemaNames(schemas, searchRequest.getSearch(), searchRequest.getSorting());
        verify(connectionMapper, times(schemas.size())).toDto(any(DatabaseSchema.class));
    }

    @Test
    void testCreateConnectionInfo() throws SQLException {
        when(mockConnection.getMetaData()).thenReturn(metaData);
        when(metaData.getCatalogs()).thenReturn(catalogsResultSet);
        when(catalogsResultSet.next()).thenReturn(true, false);

        var database = DataBase.builder().databaseName("database").build();
        when(connectionMapper.toDatabase(catalogsResultSet)).thenReturn(database);
        when(metaData.getSchemas(database.getDatabaseName(), null)).thenReturn(schemasResultSet);
        when(schemasResultSet.next()).thenReturn(true, false);

        var databaseSchema = DatabaseSchema.builder().schemaName("schema").build();
        when(connectionMapper.toSchema(schemasResultSet)).thenReturn(databaseSchema);
        when(metaData.getTables(null, databaseSchema.getSchemaName(), null, new String[]{"TABLE"})).thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(true, false);

        when(connectionMapper.toTable(tablesResultSet)).thenReturn(Table.builder().build());
        when(metaData.getColumns(any(), any(), any(), any())).thenReturn(columnsResultSet);
        when(columnsResultSet.next()).thenReturn(true, false);

        when(connectionMapper.toColumn(columnsResultSet)).thenReturn(Column.builder().build());
        when(connectionMapper.toConnectionInfo(path, metaData))
                .thenReturn(ConnectionInfo.builder().connectionPath(path).connectionHash(path.hashCode())
                        .build());

        var connectionInfo = service.createConnectionInfo(path, user, password);

        assertEquals(path, connectionInfo.getConnectionPath());
        assertEquals(path.hashCode(), connectionInfo.getConnectionHash());

        verify(metaData).getCatalogs();
        verify(catalogsResultSet, times(2)).next();
        verify(connectionMapper).toDatabase(catalogsResultSet);
        verify(metaData).getSchemas(anyString(), isNull());
        verify(schemasResultSet, times(2)).next();
        verify(connectionMapper, times(1)).toSchema(schemasResultSet);
        verify(metaData).getTables(any(), anyString(), any(), any());
        verify(tablesResultSet, times(2)).next();
        verify(connectionMapper).toTable(tablesResultSet);
        verify(metaData).getColumns(any(), any(), any(), any());
        verify(columnsResultSet, times(2)).next();
        verify(connectionMapper).toColumn(columnsResultSet);
    }

    @Test
    void testGetDatabasesFromConnection() throws SQLException {
        var searchRequest = SearchRequest.builder().search("database").build();
        var database = DataBase.builder().databaseName("database").build();
        var databases = List.of(database);
        when(mockConnection.getMetaData()).thenReturn(metaData);
        when(metaData.getCatalogs()).thenReturn(catalogsResultSet);
        when(catalogsResultSet.next()).thenReturn(true, false);

        when(connectionMapper.toDatabase(catalogsResultSet)).thenReturn(database);
        when(metaData.getSchemas(database.getDatabaseName(), null)).thenReturn(schemasResultSet);
        when(schemasResultSet.next()).thenReturn(true, false);

        var databaseSchema = DatabaseSchema.builder().schemaName("schema").build();
        when(connectionMapper.toSchema(schemasResultSet)).thenReturn(databaseSchema);
        when(metaData.getTables(null, databaseSchema.getSchemaName(), null, new String[]{"TABLE"})).thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(true, false);

        when(connectionMapper.toTable(tablesResultSet)).thenReturn(Table.builder().build());
        when(metaData.getColumns(any(), any(), any(), any())).thenReturn(columnsResultSet);
        when(columnsResultSet.next()).thenReturn(true, false);

        when(connectionMapper.toColumn(columnsResultSet)).thenReturn(Column.builder().build());
        var expectedConnectionInfo = ConnectionInfo.builder().connectionPath(path).connectionHash(path.hashCode())
                .build();
        var connectionDto = ConnectionDto.builder().path(path).hash(path.hashCode()).build();
        when(connectionMapper.toConnectionInfo(path, metaData)).thenReturn(expectedConnectionInfo);
        when(dataBasePersistence.getDatabases()).thenReturn(databases);
        when(searchService.createRegex(searchRequest)).thenReturn(searchRequest.getSearch());
        when(searchService.searchInDatabaseNames(databases, searchRequest.getSearch(), searchRequest.getSorting()))
                .thenReturn(databases);
        when(connectionMapper.toDto(any(DataBase.class))).thenReturn(DatabaseDto.builder().build());
        when(dataBasePersistence.saveConnectionInfo(expectedConnectionInfo)).thenReturn(expectedConnectionInfo);
        when(connectionMapper.toDto(expectedConnectionInfo)).thenReturn(connectionDto);
        when(dataBasePersistence.getDatabasesByHash(expectedConnectionInfo.getConnectionHash())).thenReturn(databases);

        var result = service.getDatabasesFromConnection(path, user, password, searchRequest);
        assertEquals(databases.size(), result.size());

        verify(dataBasePersistence).saveConnectionInfo(any());
        verify(searchService).createRegex(any());
        verify(searchService).searchInDatabaseNames(databases, searchRequest.getSearch(), searchRequest.getSorting());
        verify(connectionMapper, times(databases.size())).toDto(any(DataBase.class));
    }


}