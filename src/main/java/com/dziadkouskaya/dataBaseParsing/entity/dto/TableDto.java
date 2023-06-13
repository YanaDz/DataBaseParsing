package com.dziadkouskaya.dataBaseParsing.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableDto {
    private String tableName;
    private Integer columnNumber;
}
