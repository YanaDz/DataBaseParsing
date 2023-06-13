package com.dziadkouskaya.dataBaseParsing.utils;

import com.dziadkouskaya.dataBaseParsing.entity.SearchRequest;

import static com.dziadkouskaya.dataBaseParsing.utils.Constants.DEFAULT_STRING;
import static java.util.Objects.isNull;

public class Validation {
    public static SearchRequest checkSearchRequest(SearchRequest searchRequest) {
        if (isNull(searchRequest.getSearch())) {
            searchRequest.setSearch(DEFAULT_STRING);
        }
        return searchRequest;
    }
}
