package com.dziadkouskaya.dataBaseParsing.utils;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Constants {
    // exception messages
    public static final String INNER_SERVER_ERROR_MESSAGE = "SERVER ERROR";
    public static final String ACCESS_DENIED_MESSAGE = "ACCESS DENIED";

    public static final String EMPTY_STORAGE_MESSAGE = "No info in storage";

    //specific symbols
    public static final String COLON = ":";
    public static final String DOT = ".";
    public static final String STAR = "*";

    // default values
    public static final String DEFAULT_STRING = "";

}
