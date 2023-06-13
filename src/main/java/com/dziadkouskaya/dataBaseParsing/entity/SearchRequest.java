package com.dziadkouskaya.dataBaseParsing.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class SearchRequest {
    private String search;
    private Sorting sorting;
}
