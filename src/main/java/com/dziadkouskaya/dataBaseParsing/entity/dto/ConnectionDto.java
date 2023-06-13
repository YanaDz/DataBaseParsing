package com.dziadkouskaya.dataBaseParsing.entity.dto;

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
public class ConnectionDto {
    private String path;
    private String databaseType;
    private Integer databaseNumber;
    private Integer hash;
    @Builder.Default
    private List<DatabaseDto> databases = new ArrayList<>();




}
