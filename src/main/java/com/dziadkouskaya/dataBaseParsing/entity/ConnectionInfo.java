package com.dziadkouskaya.dataBaseParsing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
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

    private String databaseType;

    private Integer databaseProductVersion;

    @Builder.Default
    private List<DataBase> databases = new ArrayList<>();

}
