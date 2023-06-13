package com.dziadkouskaya.dataBaseParsing.exception;

import java.sql.SQLException;

public class DatabaseConnectionException extends SQLException {
    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseConnectionException(Throwable cause) {
        super(cause);
    }

    public DatabaseConnectionException() {
    }
}
