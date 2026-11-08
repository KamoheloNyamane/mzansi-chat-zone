package com.mzansi.chatzone.dao;

/**
 * Unchecked wrapper around {@link java.sql.SQLException}. DAOs throw this
 * instead of forcing every servlet up the call stack to declare or catch
 * SQLException. Servlets catch it at the boundary and turn it into a
 * proper error response.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
