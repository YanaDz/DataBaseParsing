package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.DatabaseSchema;
import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.Sorting;

import java.util.List;

public interface SearchService {
    String createRegex(SearchRequest searchRequest);
    List<DataBase> searchInDatabaseNames(List<DataBase> dataBases, String regex, Sorting sort);

    List<DatabaseSchema> searchInSchemaNames(List<DatabaseSchema> dataBases, String regex, Sorting sort);
}
