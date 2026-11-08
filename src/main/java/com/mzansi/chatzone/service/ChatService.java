package com.mzansi.chatzone.service;

import com.mzansi.chatzone.dao.ConversationDao;
import com.mzansi.chatzone.dao.MessageDao;
import com.mzansi.chatzone.model.Message;
import com.mzansi.chatzone.model.MessageType;
import com.mzansi.chatzone.websocket.ChatMessagePayload;

import java.util.List;

/**
 * Persists messages and hands back the same {@link ChatMessagePayload}
 * shape regardless of whether the message arrived over the WebSocket
 * (text) or an HTTP multipart upload (a captured photo) — both
 * {@code ChatEndpoint} and {@code ImageUploadServlet} call this, then
 * both push the result to {@code SessionRegistry.broadcast}. This class
 * knows nothing about WebSocket delivery itself; that separation is
 * deliberate.
 */
public class ChatService {

    private final ConversationDao conversationDao;
    private final MessageDao messageDao;

    public ChatService() {
        this.conversationDao = new ConversationDao();
        this.messageDao = new MessageDao();
    }

    public ChatService(ConversationDao conversationDao, MessageDao messageDao) {
        this.conversationDao = conversationDao;
        this.messageDao = messageDao;
    }

    public boolean isParticipant(int conversationId, int userId) {
        return conversationDao.isParticipant(conversationId, userId);
    }

    public Message sendText(int conversationId, int senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("Message cannot be empty.");
        }
        if (content.length() > 2000) {
            throw new ValidationException("Message is too long.");
        }
        return messageDao.insertText(conversationId, senderId, content.trim());
    }

    public Message sendImage(int conversationId, int senderId, String imagePath) {
        return messageDao.insertImage(conversationId, senderId, imagePath);
    }

    public List<Message> history(int conversationId) {
        return messageDao.findByConversation(conversationId);
    }

    public ChatMessagePayload toPayload(Message message) {
        ChatMessagePayload payload = new ChatMessagePayload();
        payload.setType(message.getType().name());
        payload.setConversationId(message.getConversationId());
        payload.setMessageId(message.getId());
        payload.setSenderUsername(message.getSenderUsername());
        payload.setSenderDisplayName(message.getSenderDisplayName());
        if (message.getType() == MessageType.TEXT) {
            payload.setContent(message.getContent());
        } else {
            payload.setImagePath(message.getImagePath());
        }
        payload.setSentAt(message.getSentAt() != null ? message.getSentAt().toString() : null);
        return payload;
    }
}
