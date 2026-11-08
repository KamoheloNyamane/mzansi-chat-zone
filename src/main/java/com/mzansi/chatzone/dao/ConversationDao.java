package com.mzansi.chatzone.dao;

import com.mzansi.chatzone.model.ConversationSummary;
import com.mzansi.chatzone.model.MessageType;
import com.mzansi.chatzone.model.User;
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
 * Data access for {@code conversations} / {@code conversation_participants},
 * plus a read model ({@link ConversationSummary}) for the inbox list.
 */
public class ConversationDao {

    /**
     * Returns the id of the existing 1:1 conversation between these two
     * users, or creates one (and both participant rows) if none exists yet.
     * Runs as a single transaction so two near-simultaneous "New Chat"
     * clicks can't create two duplicate threads.
     */
    public int findOrCreateOneToOne(int userIdA, int userIdB) {
        String findSql =
                "SELECT cp1.conversation_id FROM conversation_participants cp1 " +
                "JOIN conversation_participants cp2 ON cp1.conversation_id = cp2.conversation_id " +
                "WHERE cp1.user_id = ? AND cp2.user_id = ? " +
                "AND (SELECT COUNT(*) FROM conversation_participants cp3 " +
                "     WHERE cp3.conversation_id = cp1.conversation_id) = 2";

        try (Connection conn = DataSourceProvider.get().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                    ps.setInt(1, userIdA);
                    ps.setInt(2, userIdB);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int existingId = rs.getInt(1);
                            conn.commit();
                            return existingId;
                        }
                    }
                }

                int newConversationId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO conversations () VALUES ()", Statement.RETURN_GENERATED_KEYS)) {
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        newConversationId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO conversation_participants (conversation_id, user_id) VALUES (?, ?)")) {
                    ps.setInt(1, newConversationId);
                    ps.setInt(2, userIdA);
                    ps.addBatch();
                    ps.setInt(1, newConversationId);
                    ps.setInt(2, userIdB);
                    ps.addBatch();
                    ps.executeBatch();
                }

                conn.commit();
                return newConversationId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find or create conversation", e);
        }
    }

    public boolean isParticipant(int conversationId, int userId) {
        String sql = "SELECT 1 FROM conversation_participants WHERE conversation_id = ? AND user_id = ?";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check conversation membership", e);
        }
    }

    /**
     * All conversations the given user belongs to, each paired with the
     * other participant and a preview of the most recent message, newest
     * first. One query — no N+1 lookups per row.
     */
    public List<ConversationSummary> listForUser(int userId) {
        String sql =
                "SELECT c.id AS conversation_id, " +
                "       other.username AS other_username, other.display_name AS other_display_name, " +
                "       lm.message_type AS last_type, lm.content AS last_content, lm.sent_at AS last_sent_at " +
                "FROM conversations c " +
                "JOIN conversation_participants mine ON mine.conversation_id = c.id AND mine.user_id = ? " +
                "JOIN conversation_participants other_cp ON other_cp.conversation_id = c.id AND other_cp.user_id <> ? " +
                "JOIN users other ON other.id = other_cp.user_id " +
                "LEFT JOIN messages lm ON lm.id = ( " +
                "    SELECT m2.id FROM messages m2 WHERE m2.conversation_id = c.id " +
                "    ORDER BY m2.sent_at DESC, m2.id DESC LIMIT 1) " +
                "ORDER BY lm.sent_at DESC, c.id DESC";

        List<ConversationSummary> results = new ArrayList<>();
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConversationSummary summary = new ConversationSummary();
                    summary.setConversationId(rs.getInt("conversation_id"));
                    summary.setOtherUsername(rs.getString("other_username"));
                    summary.setOtherDisplayName(rs.getString("other_display_name"));

                    String type = rs.getString("last_type");
                    if (type != null) {
                        MessageType messageType = MessageType.valueOf(type);
                        summary.setLastMessageType(messageType);
                        summary.setLastMessagePreview(
                                messageType == MessageType.IMAGE ? "Sent a photo" : rs.getString("last_content"));
                    }
                    Timestamp sentAt = rs.getTimestamp("last_sent_at");
                    if (sentAt != null) {
                        summary.setLastMessageAt(sentAt.toLocalDateTime());
                    }
                    results.add(summary);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list conversations for user", e);
        }
        return results;
    }

    /** The other person in a 1:1 thread — used to label the conversation/camera page header. */
    public User findOtherParticipant(int conversationId, int selfUserId) {
        String sql =
                "SELECT u.id, u.username, u.display_name, u.password_hash, u.created_at " +
                "FROM conversation_participants cp " +
                "JOIN users u ON u.id = cp.user_id " +
                "WHERE cp.conversation_id = ? AND cp.user_id <> ? LIMIT 1";
        try (Connection conn = DataSourceProvider.get().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, selfUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setDisplayName(rs.getString("display_name"));
                user.setPasswordHash(rs.getString("password_hash"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                }
                return user;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find the other participant", e);
        }
    }
}
