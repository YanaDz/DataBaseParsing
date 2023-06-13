package com.dziadkouskaya.dataBaseParsing.service;

import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.DatabaseSchema;
import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;
import com.dziadkouskaya.dataBaseParsing.entity.Sorting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dziadkouskaya.dataBaseParsing.utils.Constants.DOT;
import static com.dziadkouskaya.dataBaseParsing.utils.Constants.STAR;
import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    @Override
    public String createRegex(SearchRequest searchRequest) {
        return DOT + STAR + searchRequest.getSearch() + DOT + STAR;
    }

    @Override
    public List<DataBase> searchInDatabaseNames(List<DataBase> dataBases, String regex, Sorting sort) {
        return isNull(sort)
            ? searchInNamesWithoutSort(dataBases, regex)
            : searchInNamesWithSort(dataBases, regex, sort);
    }

    private List<DataBase> searchInNamesWithoutSort(List<DataBase> dataBases, String regex) {
        return dataBases.stream()
            .filter(database -> Pattern.matches(regex, database.getDatabaseName()))
            .collect(Collectors.toList());
    }

    private List<DataBase> searchInNamesWithSort(List<DataBase> dataBases, String regex, Sorting sort) {
        return sort == Sorting.ASC
            ? dataBases.stream()
            .filter(database -> Pattern.matches(regex, database.getDatabaseName()))
            .sorted(Comparator.comparing(DataBase::getDatabaseName))
            .collect(Collectors.toList())
            : dataBases.stream()
            .filter(database -> Pattern.matches(regex, database.getDatabaseName()))
            .sorted(Comparator.comparing(DataBase::getDatabaseName).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public List<DatabaseSchema> searchInSchemaNames(List<DatabaseSchema> dataBases, String regex, Sorting sort) {
        return isNull(sort)
            ? searchInSchemaNamesWithoutSort(dataBases, regex)
            : searchInSchemasNamesWithSort(dataBases, regex, sort);
    }

    private List<DatabaseSchema> searchInSchemaNamesWithoutSort(List<DatabaseSchema> dataBases, String regex) {
        return dataBases.stream()
            .filter(schema -> Pattern.matches(regex, schema.getSchemaName()))
            .collect(Collectors.toList());
    }

    private List<DatabaseSchema> searchInSchemasNamesWithSort(List<DatabaseSchema> schemas, String regex, Sorting sort) {
        return sort == Sorting.ASC
            ? schemas.stream()
            .filter(schema -> Pattern.matches(regex, schema.getSchemaName()))
            .sorted(Comparator.comparing(DatabaseSchema::getSchemaName))
            .collect(Collectors.toList())
            : schemas.stream()
            .filter(schema -> Pattern.matches(regex, schema.getSchemaName()))
            .sorted(Comparator.comparing(DatabaseSchema::getSchemaName).reversed())
            .collect(Collectors.toList());
    }
}
