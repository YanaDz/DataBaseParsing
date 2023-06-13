package com.dziadkouskaya.dataBaseParsing.service.mapper;

import com.dziadkouskaya.dataBaseParsing.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConnectionMapper {

    public static final String PARAM_DATABASE = "TABLE_CAT";

    public static final String PARAM_TABLE = "TABLE_NAME";

    public static final String PARAM_SCHEMA = "TABLE_SCHEM";
    public static final String PARAM_COLUMN_NAME = "COLUMN_NAME";
    public static final String PARAM_COLUMN_TYPE = "TYPE_NAME";

    @Mapping(target = "connectionPath", source = "path")
    @Mapping(target = "connectionHash", expression = "java(path.hashCode())" )
    @Mapping(target = "databaseName", expression = "java(meta.getDatabaseProductName())")
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



}
