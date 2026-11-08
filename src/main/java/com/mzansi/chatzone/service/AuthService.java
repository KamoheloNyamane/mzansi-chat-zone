package com.mzansi.chatzone.service;

import com.mzansi.chatzone.dao.UserDao;
import com.mzansi.chatzone.model.User;
import com.mzansi.chatzone.security.PasswordUtil;

import java.util.regex.Pattern;

/**
 * Registration and login rules. Servlets never talk to {@link UserDao}
 * or {@link PasswordUtil} directly — everything about what makes a valid
 * account and a valid login lives in exactly one place.
 */
public class AuthService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,50}$");

    private final UserDao userDao;

    public AuthService() {
        this.userDao = new UserDao();
    }

    /** Visible for testing — allows a fake DAO to be injected. */
    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(String username, String displayName, char[] password) {
        username = username == null ? "" : username.trim();
        displayName = displayName == null ? "" : displayName.trim();

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException(
                    "Username must be 3-50 characters: letters, numbers, dots, underscores or hyphens only.");
        }
        if (displayName.isEmpty()) {
            throw new ValidationException("Display name is required.");
        }
        if (password == null || password.length < 8) {
            throw new ValidationException("Password must be at least 8 characters.");
        }
        if (userDao.existsByUsername(username)) {
            throw new ValidationException("That username is already taken.");
        }

        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setPasswordHash(PasswordUtil.hash(password));
        return userDao.create(user);
    }

    /** Returns the authenticated user, or {@code null} if the credentials don't match. */
    public User authenticate(String username, char[] password) {
        if (username == null || password == null) {
            return null;
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            return null;
        }
        return PasswordUtil.verify(password, user.getPasswordHash()) ? user : null;
    }
}
