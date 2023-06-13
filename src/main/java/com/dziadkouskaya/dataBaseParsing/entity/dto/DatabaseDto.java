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
@AllArgsConstructor
public class DatabaseDto {
    private String databaseName;
    private Integer schemaNumber;
    @Builder.Default
    List<SchemaDto> schemas  = new ArrayList<>();


}
