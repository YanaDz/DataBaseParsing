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
public class SchemaDto {
    private String schemaName;
    private Integer tableNumber;
    @Builder.Default
    List<TableDto> tables = new ArrayList<>();
}
