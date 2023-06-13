package com.dziadkouskaya.dataBaseParsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionInfo {
    private Integer connectionHash;

    private String connectionPath;

    private String databaseName;

    private String databaseType;

    private Integer databaseProductVersion;

    private List<DataBase> databases = new ArrayList<>();

}
