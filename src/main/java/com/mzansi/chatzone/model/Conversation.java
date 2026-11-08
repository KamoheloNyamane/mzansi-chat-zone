package com.mzansi.chatzone.model;

import java.time.LocalDateTime;

/**
 * A conversation thread. Participation is tracked separately in
 * {@code conversation_participants} so a thread can later grow beyond
 * two people without a schema change.
 */
public class Conversation {

    private int id;
    private LocalDateTime createdAt;

    public Conversation() {
    }

    public Conversation(int id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
