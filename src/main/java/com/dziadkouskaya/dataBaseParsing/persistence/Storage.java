package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import lombok.Getter;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class Storage {
    private Map<Integer, ConnectionInfo> connections = new HashMap<>();

    public ConnectionInfo persistConnectionInfo(ConnectionInfo connectionInfo) {
        connections.put(connectionInfo.getConnectionHash(), connectionInfo);
        return connectionInfo;
    }

}
