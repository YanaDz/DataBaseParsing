package com.dziadkouskaya.dataBaseParsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Column {
    private String columnName;
    private String columnType;

    public void toUpperCase() {
        this.columnName = this.columnName.toUpperCase();
        this.columnType = this.columnType.toUpperCase();
    }
}
