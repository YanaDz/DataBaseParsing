package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;

@Component
@RequiredArgsConstructor
public class DataBasePersistenceImpl implements DataBasePersistence {
    private final Storage storage;

    @Override
    public ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo) {
        return storage.persistConnectionInfo(connectionInfo);
    }
}
