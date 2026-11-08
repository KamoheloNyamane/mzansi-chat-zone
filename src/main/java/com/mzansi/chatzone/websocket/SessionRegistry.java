package com.mzansi.chatzone.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tracks every currently-open {@link ChatEndpoint} session and broadcasts
 * outgoing frames to all of them. This is the literal "WebSocket server
 * endpoint responsible for receiving messages and broadcasting them to
 * all connected clients" the brief asks for: every open connection
 * receives every message, regardless of which conversation it belongs
 * to — the browser (chat.js) decides what to do with a frame that isn't
 * for the conversation currently on screen (update the inbox preview,
 * or the frame if it is).
 *
 * <p>Application-scoped singleton, not one instance per endpoint — JSR 356
 * creates a new {@link ChatEndpoint} instance per connection, so shared
 * state has to live somewhere else.</p>
 */
public final class SessionRegistry {

    private static final Logger LOGGER = Logger.getLogger(SessionRegistry.class.getName());
    private static final SessionRegistry INSTANCE = new SessionRegistry();

    // username -> that user's open sessions (a person can have more than one tab open)
    private final Map<String, Set<Session>> sessionsByUsername = new ConcurrentHashMap<>();

    private SessionRegistry() {
    }

    public static SessionRegistry getInstance() {
        return INSTANCE;
    }

    public void register(String username, Session session) {
        sessionsByUsername
                .computeIfAbsent(username, key -> new CopyOnWriteArraySet<>())
                .add(session);
    }

    public void unregister(String username, Session session) {
        Set<Session> sessions = sessionsByUsername.get(username);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionsByUsername.remove(username, sessions);
            }
        }
    }

    /** Sends {@code payload} to every currently-open session, dropping any that have gone stale. */
    public void broadcast(ChatMessagePayload payload) {
        for (Set<Session> sessions : sessionsByUsername.values()) {
            for (Session session : sessions) {
                if (!session.isOpen()) {
                    continue;
                }
                try {
                    session.getBasicRemote().sendObject(payload);
                } catch (IOException | EncodeException e) {
                    LOGGER.log(Level.WARNING, "Failed to deliver message to session " + session.getId(), e);
                }
            }
        }
    }
}
