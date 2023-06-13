package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.Column;
import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.DatabaseSchema;
import com.dziadkouskaya.dataBaseParsing.entity.Table;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.exception.DatabaseConnectionException;
import com.dziadkouskaya.dataBaseParsing.persistence.DataBasePersistence;
import com.dziadkouskaya.dataBaseParsing.service.mapper.ConnectionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@PrepareForTest(DriverManager.class)
class DataBaseParsingServiceImplTest {
    @Mock
    private DatabaseMetaData metaData;

    @Mock
    private ConnectionMapper connectionMapper;

    @Mock
    private DataBasePersistence dataBasePersistence;

    @Mock
    private ResultSet catalogsResultSet;

    @Mock
    private ResultSet schemasResultSet;

    @Mock
    private ResultSet tablesResultSet;

    @Mock
    private ResultSet columnsResultSet;

    @Mock
    private Connection mockConnection;

    @InjectMocks
    private DataBaseParsingServiceImpl dataBaseParsingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDatabases() throws SQLException {
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

        List<DataBase> databases = dataBaseParsingService.createDatabases(metaData);

        assertEquals(1, databases.size());
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
    void getConnectionInfoFromPathTest() throws SQLException {
        String path = "jdbc:postgresql://localhost:5432/dbname";
        String user = "username";
        String password = "password";
        ConnectionInfo connectionInfo = ConnectionInfo.builder().build();
        ConnectionDto expectedConnectionDto = ConnectionDto.builder().build();

        try (MockedStatic<DriverManager> utilities = mockStatic(DriverManager.class)) {
            utilities.when(() -> DriverManager.getConnection(path, user, password))
                .thenReturn(any(Connection.class));
        }

        when(DriverManager.getConnection(path, user, password)).thenReturn(mockConnection);
        when(dataBaseParsingService.createConnectionInfo(path, user, password)).thenReturn(connectionInfo);
        when(dataBasePersistence.saveConnectionInfo(connectionInfo)).thenReturn(connectionInfo);
        when(connectionMapper.toDto(connectionInfo)).thenReturn(expectedConnectionDto);

        ConnectionDto actualConnectionDto = dataBaseParsingService.getConnectionInfoFromPath(path, user, password);

        verify(dataBaseParsingService).createConnectionInfo(path, user, password);
        verify(dataBasePersistence).saveConnectionInfo(connectionInfo);
        verify(connectionMapper).toDto(connectionInfo);
        assertEquals(expectedConnectionDto, actualConnectionDto);
    }
}