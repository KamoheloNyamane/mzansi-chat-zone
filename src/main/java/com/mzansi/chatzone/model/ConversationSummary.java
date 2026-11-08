package com.mzansi.chatzone.model;

import java.time.LocalDateTime;

/**
 * A read-model for the conversations list: everything that page needs
 * to render one row, pre-joined so the servlet issues one query instead
 * of N+1. Not persisted — built by {@code ConversationDao} at read time.
 */
public class ConversationSummary {

    private int conversationId;
    private String otherUsername;
    private String otherDisplayName;
    private String lastMessagePreview;
    private MessageType lastMessageType;
    private LocalDateTime lastMessageAt;

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getOtherUsername() {
        return otherUsername;
    }

    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }

    public String getOtherDisplayName() {
        return otherDisplayName;
    }

    public void setOtherDisplayName(String otherDisplayName) {
        this.otherDisplayName = otherDisplayName;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public MessageType getLastMessageType() {
        return lastMessageType;
    }

    public void setLastMessageType(MessageType lastMessageType) {
        this.lastMessageType = lastMessageType;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
