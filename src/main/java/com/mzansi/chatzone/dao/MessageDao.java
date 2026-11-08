package com.mzansi.chatzone.dao;

import com.mzansi.chatzone.model.Message;
import com.mzansi.chatzone.model.MessageType;
import com.mzansi.chatzone.util.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for {@code messages}. Reads join against {@code users} so
 * every {@link Message} comes back with the sender's username and display
 * name already attached — the presentation layer never needs a second
 * lookup just to label a bubble.
 */
public class MessageDao {

    public Message insertText(int conversationId, int senderId, String content) {
        return insert(conversationId, senderId, MessageType.TEXT, content, null);
    }

    public Message insertImage(int conversationId, int senderId, String imagePath) {
        return insert(conversationId, senderId, MessageType.IMAGE, null, imagePath);
    }

    private Message insert(int conversationId, int senderId, MessageType type, String content, String imagePath) {
        String sql = "INSERT INTO messages (conversation_id, sender_id, message_type, content, image_path) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, senderId);
            ps.setString(3, type.name());
            ps.setString(4, content);
            ps.setString(5, imagePath);
            ps.executeUpdate();

            long id;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                id = keys.getLong(1);
            }
            // Re-read through the joined query so the returned object carries
            // the sender's username/display name and the DB-assigned timestamp,
            // exactly as any other caller of findByConversation would see it.
            return findById(id);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert message", e);
        }
    }

    public Message findById(long id) {
        String sql = baseSelect() + " WHERE m.id = ?";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load message by id", e);
        }
    }

    /** Full history for a conversation, oldest first — ready to render top-to-bottom. */
    public List<Message> findByConversation(int conversationId) {
        String sql = baseSelect() + " WHERE m.conversation_id = ? ORDER BY m.sent_at ASC, m.id ASC";
        List<Message> messages = new ArrayList<>();
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load conversation history", e);
        }
        return messages;
    }

    private String baseSelect() {
        return "SELECT m.id, m.conversation_id, m.sender_id, m.message_type, m.content, m.image_path, m.sent_at, "
                + "u.username AS sender_username, u.display_name AS sender_display_name "
                + "FROM messages m JOIN users u ON u.id = m.sender_id";
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setConversationId(rs.getInt("conversation_id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setSenderUsername(rs.getString("sender_username"));
        message.setSenderDisplayName(rs.getString("sender_display_name"));
        message.setType(MessageType.valueOf(rs.getString("message_type")));
        message.setContent(rs.getString("content"));
        message.setImagePath(rs.getString("image_path"));
        Timestamp sentAt = rs.getTimestamp("sent_at");
        if (sentAt != null) {
            message.setSentAt(sentAt.toLocalDateTime());
        }
        return message;
    }
}
