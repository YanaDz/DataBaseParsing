package com.dziadkouskaya.dataBaseParsing.service.mapper;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ConnectionMapperTest {
    @Mock
    private DatabaseMetaData databaseMetaData;
    @InjectMocks
    private ConnectionMapper connectionMapper = new ConnectionMapperImpl();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    static Stream<Arguments> argsForGetConnectionInfo() {
        return Stream.of(
            arguments("jdbc:postgresql://localhost:5432/dbname", "PostgreSQL", 15)
        );
    }
    @ParameterizedTest
    @MethodSource("argsForGetConnectionInfo")
    void testToConnectionInfo(String path, String dbProductName, int dbVersion) throws SQLException {
        when(databaseMetaData.getDatabaseProductName()).thenReturn(dbProductName);
        when(databaseMetaData.getDatabaseMajorVersion()).thenReturn(dbVersion);

        ConnectionInfo result = connectionMapper.toConnectionInfo(path, databaseMetaData);

        assertEquals(path, result.getConnectionPath());
        assertEquals(path.hashCode(), result.getConnectionHash());
        assertEquals(dbProductName, result.getDatabaseType());
        assertEquals(dbVersion, result.getDatabaseProductVersion());

        verify(databaseMetaData).getDatabaseProductName();
        verify(databaseMetaData).getDatabaseMajorVersion();
        verifyNoMoreInteractions(databaseMetaData);
    }
}
