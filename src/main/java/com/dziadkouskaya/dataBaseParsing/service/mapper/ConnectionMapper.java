package com.dziadkouskaya.dataBaseParsing.service.mapper;

import com.dziadkouskaya.dataBaseParsing.entity.*;
import com.dziadkouskaya.dataBaseParsing.entity.dto.ConnectionDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.DatabaseDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.SchemaDto;
import com.dziadkouskaya.dataBaseParsing.entity.dto.TableDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConnectionMapper {

    public static final String PARAM_DATABASE = "TABLE_CAT";

    public static final String PARAM_TABLE = "TABLE_NAME";

    public static final String PARAM_SCHEMA = "TABLE_SCHEM";
    public static final String PARAM_COLUMN_NAME = "COLUMN_NAME";
    public static final String PARAM_COLUMN_TYPE = "TYPE_NAME";

    @Mapping(target = "connectionPath", source = "path")
    @Mapping(target = "connectionHash", expression = "java(path.hashCode())" )
    @Mapping(target = "databaseType", expression = "java(meta.getDatabaseProductName())")
    @Mapping(target = "databaseProductVersion", expression = "java(meta.getDatabaseMajorVersion())")
    ConnectionInfo toConnectionInfo(String path, DatabaseMetaData meta) throws SQLException;

    @Mapping(target = "databaseName", expression = "java(resultSet.getString(PARAM_DATABASE))")
    DataBase toDatabase(ResultSet resultSet) throws SQLException;

    @Mapping(target = "schemaName", expression = "java(resultSet.getString(PARAM_SCHEMA))")
    DatabaseSchema toSchema(ResultSet resultSet) throws SQLException;

    @Mapping(target = "tableName", expression = "java(resultSet.getString(PARAM_TABLE))")
    Table toTable(ResultSet resultSet) throws SQLException;

    @Mapping(target = "columnName", expression = "java(resultSet.getString(PARAM_COLUMN_NAME))")
    @Mapping(target = "columnType", expression = "java(resultSet.getString(PARAM_COLUMN_TYPE))")
    Column toColumn(ResultSet resultSet) throws SQLException;

    @Mapping(target = "tableName", source = "table.tableName")
    @Mapping(target = "columnNumber", expression = "java(table.getColumns().size())")
    TableDto toDto(Table table);

    @Mapping(target = "schemaName", source = "schema.schemaName")
    @Mapping(target = "tableNumber", expression = "java(schema.getTables().size())")
    @Mapping(target = "tables", expression = "java(getTableDtos(schema))")
    SchemaDto toDto(DatabaseSchema schema);

    @Mapping(target = "databaseName", source = "dataBase.databaseName")
    @Mapping(target = "schemaNumber", expression = "java(dataBase.getSchemas().size())")
    @Mapping(target = "schemas", expression = "java(getSchemaDtos(dataBase))")
    DatabaseDto toDto(DataBase dataBase);

    @Mapping(target = "path", source = "connection.connectionPath")
    @Mapping(target = "databaseType", source = "connection.databaseType")
    @Mapping(target = "databaseNumber", expression = "java(connection.getDatabases().size())")
    @Mapping(target = "databases", expression = "java(getDatabaseDtos(connection))")
    ConnectionDto toDto(ConnectionInfo connection);

    default List<TableDto> getTableDtos(DatabaseSchema schema) {
        return schema.getTables().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    default List<SchemaDto> getSchemaDtos(DataBase dataBase) {
        return dataBase.getSchemas().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    default List<DatabaseDto> getDatabaseDtos(ConnectionInfo connectionInfo) {
        return connectionInfo.getDatabases().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}
