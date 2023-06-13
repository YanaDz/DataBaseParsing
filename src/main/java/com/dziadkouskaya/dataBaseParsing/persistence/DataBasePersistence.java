package com.dziadkouskaya.dataBaseParsing.persistence;

import com.dziadkouskaya.dataBaseParsing.entity.ConnectionInfo;

public interface DataBasePersistence {
    ConnectionInfo saveConnectionInfo(ConnectionInfo connectionInfo);
}
