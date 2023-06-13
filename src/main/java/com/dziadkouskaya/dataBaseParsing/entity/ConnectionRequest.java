package com.dziadkouskaya.dataBaseParsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequest {
    private String connection;
    private String user;
    private String password;
}
