package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;
import com.dziadkouskaya.dataBaseParsing.entity.DataBase;
import com.dziadkouskaya.dataBaseParsing.entity.DatabaseSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataBasePersistenceImpl implements DataBasePersistence {
    private final Storage storage;

    @Override
    public Map<Integer, ConnectionInfo> getConnections() {
        return storage.getConnections();
    }

    @Override
    public ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo) {
        return storage.persistConnectionInfo(connectionInfo);
    }

    @Override
    public List<DataBase> getDatabases() {
        return storage.getConnections()
            .values().stream()
            .map(ConnectionInfo::getDatabases)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @Override
    public List<DatabaseSchema> getSchemas() {
        return storage.getConnections()
            .values().stream()
            .map(ConnectionInfo::getDatabases)
            .flatMap(List::stream)
            .map(DataBase::getSchemas)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @Override
    public List<DataBase> getDatabasesByHash(Integer hash) {
        return storage.getConnections().get(hash).getDatabases();
    }

    @Override
    public List<DatabaseSchema> getSchemasByHash(Integer hash) {
        return storage.getConnections().get(hash).getDatabases()
            .stream()
            .map(DataBase::getSchemas)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
