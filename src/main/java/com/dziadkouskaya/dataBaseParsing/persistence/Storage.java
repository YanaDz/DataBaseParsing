package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Storage {
    private Map<String, Integer> hashes = new HashMap<>();

    private Map<Integer, ConnectionInfo> connections = new HashMap<>();


    public ConnectionInfo persistConnectionInfo(ConnectionInfo connectionInfo) {
        connections.put(connectionInfo.getConnectionHash(), connectionInfo);
        System.out.println("Storage connections:" + connections);
        return connectionInfo;
    }
}
