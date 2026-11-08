package com.mzansi.chatzone.websocket;

/**
 * The JSON shape that travels over the WebSocket wire, in both
 * directions. Kept separate from {@link com.mzansi.chatzone.model.Message}
 * on purpose: the wire protocol and the persistence model are allowed to
 * evolve independently, and an incoming client frame should only ever
 * carry the few fields a client is trusted to set.
 *
 * <p>Inbound (client -&gt; server) frames only need {@code type},
 * {@code conversationId} and {@code content} — the server fills in
 * {@code senderUsername} itself from the authenticated session, never
 * from client input. Outbound (server -&gt; client) frames carry every
 * field so the browser can render a bubble without a follow-up request.</p>
 */
public class ChatMessagePayload {

    private String type;             // "TEXT" | "IMAGE" | "ERROR"
    private int conversationId;
    private long messageId;
    private String senderUsername;
    private String senderDisplayName;
    private String content;
    private String imagePath;
    private String sentAt;           // ISO-8601, set server-side only

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}
