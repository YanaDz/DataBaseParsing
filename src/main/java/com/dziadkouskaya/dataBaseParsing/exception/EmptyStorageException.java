package com.dziadkouskaya.dataBaseParsing.exception;

public class EmptyStorageException extends ApplicationException {
    public EmptyStorageException(String message) {
        super(message);
    }

    public EmptyStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyStorageException(Throwable cause) {
        super(cause);
    }

    public EmptyStorageException() {
    }
}
