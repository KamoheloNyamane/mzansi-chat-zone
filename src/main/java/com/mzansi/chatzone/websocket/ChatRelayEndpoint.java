package com.mzansi.chatzone.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Real-time WebSocket relay endpoint at /chat-endpoint.
 * Allows Android physical devices and web clients outside the emulator to exchange messages.
 * Broadcasts all incoming messages to all connected sessions.
 */
@ServerEndpoint("/chat-endpoint")
public class ChatRelayEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ChatRelayEndpoint.class.getName());
    private static final Set<Session> SESSIONS = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        LOGGER.info("Client connected to /chat-endpoint: " + session.getId() + " (Total active: " + SESSIONS.size() + ")");
    }

    @OnMessage
    public void onMessage(String message, Session senderSession) {
        LOGGER.info("Relaying message from session " + senderSession.getId() + ": " + message);
        for (Session session : SESSIONS) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to broadcast to session: " + session.getId(), e);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        LOGGER.info("Client disconnected from /chat-endpoint: " + session.getId() + " (Remaining: " + SESSIONS.size() + ")");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        if (session != null) {
            SESSIONS.remove(session);
        }
        LOGGER.log(Level.WARNING, "WebSocket error on /chat-endpoint", error);
    }
}
