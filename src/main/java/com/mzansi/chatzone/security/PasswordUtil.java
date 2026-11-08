package com.mzansi.chatzone.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Thin wrapper around the BCrypt library. This is the only class in the
 * application allowed to see a plain-text password — everything else
 * passes hashes around.
 */
public final class PasswordUtil {

    /** Work factor 12 — the current recommended floor for BCrypt in 2026. */
    private static final int COST_FACTOR = 12;

    private PasswordUtil() {
    }

    public static String hash(char[] plainTextPassword) {
        return BCrypt.withDefaults().hashToString(COST_FACTOR, plainTextPassword);
    }

    public static boolean verify(char[] plainTextPassword, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainTextPassword, storedHash);
        return result.verified;
    }
}
