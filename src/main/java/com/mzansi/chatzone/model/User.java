package com.mzansi.chatzone.model;

import java.time.LocalDateTime;

/**
 * A registered account. {@code passwordHash} holds a BCrypt digest only —
 * plain-text passwords are never read back from the database or held on
 * this object beyond the moment of hashing/verification.
 */
public class User {

    private int id;
    private String username;
    private String displayName;
    private String passwordHash;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(int id, String username, String displayName, String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
