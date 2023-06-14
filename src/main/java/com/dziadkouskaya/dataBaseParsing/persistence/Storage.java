package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.dziadkouskaya.dataBaseParsing.configuration.CachingConfig.CONNECTIONS;

@Slf4j
@Component
@Setter
public class Storage {
    private final CacheManager cacheManager;

    public Storage(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Cacheable(CONNECTIONS)
    public Map<Integer, ConnectionInfo> getConnections() {
        return (Map<Integer, ConnectionInfo>) cacheManager.getCache(CONNECTIONS).get(CONNECTIONS, Map.class);
    }

    @CachePut(CONNECTIONS)
    public ConnectionInfo persistConnectionInfo(ConnectionInfo connectionInfo) {
        var map = Map.of(connectionInfo.getConnectionHash(), connectionInfo);
        cacheManager.getCache(CONNECTIONS).put(CONNECTIONS, Map.of(connectionInfo.getConnectionHash(), connectionInfo));

        return connectionInfo;
    }

}
