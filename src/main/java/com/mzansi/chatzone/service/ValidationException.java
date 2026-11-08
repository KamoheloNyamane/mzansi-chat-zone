package com.mzansi.chatzone.service;

/**
 * A validation failure whose message is safe to show directly to the
 * user (e.g. "That username is already taken"). Servlets catch this at
 * the boundary and re-render the form with the message attached, instead
 * of failing the whole request.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
