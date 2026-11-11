package com.littlehorizons.erp.util;

/**
 * Wraps database errors (SQLException) with a message the user can read.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String userMessage) {
        super(userMessage);
    }

    public DataAccessException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }
}
