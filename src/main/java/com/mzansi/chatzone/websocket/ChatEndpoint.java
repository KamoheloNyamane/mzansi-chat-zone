package com.mzansi.chatzone.websocket;

import com.mzansi.chatzone.model.Message;
import com.mzansi.chatzone.service.ChatService;
import com.mzansi.chatzone.service.ValidationException;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The single WebSocket server endpoint the brief asks for: it receives
 * chat messages and broadcasts them to every connected client
 * ({@link SessionRegistry#broadcast}). One instance of this class is
 * created per connection (that's how JSR 356 works), so all shared state
 * — who's connected — lives in the {@link SessionRegistry} singleton,
 * not on {@code this}.
 */
@ServerEndpoint(
        value = "/chat",
        configurator = HttpSessionConfigurator.class,
        encoders = {MessageEncoder.class},
        decoders = {MessageDecoder.class}
)
public class ChatEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ChatEndpoint.class.getName());

    private final ChatService chatService = new ChatService();

    private String username;
    private int userId;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        Object sessionUsername = httpSession == null ? null : httpSession.getAttribute("username");
        Object sessionUserId = httpSession == null ? null : httpSession.getAttribute("userId");

        if (sessionUsername == null || sessionUserId == null) {
            closeQuietly(session, CloseReason.CloseCodes.VIOLATED_POLICY, "Login required");
            return;
        }

        this.username = (String) sessionUsername;
        this.userId = (Integer) sessionUserId;
        SessionRegistry.getInstance().register(username, session);
    }

    @OnMessage
    public void onMessage(ChatMessagePayload incoming, Session session) {
        if (username == null) {
            return; // onOpen already rejected this session; nothing to do
        }
        try {
            if (!chatService.isParticipant(incoming.getConversationId(), userId)) {
                LOGGER.warning("User " + username + " tried to post into a conversation they don't belong to");
                return;
            }
            Message saved = chatService.sendText(incoming.getConversationId(), userId, incoming.getContent());
            SessionRegistry.getInstance().broadcast(chatService.toPayload(saved));
        } catch (ValidationException e) {
            sendError(session, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to handle incoming chat message", e);
            sendError(session, "Your message could not be sent. Please try again.");
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (username != null) {
            SessionRegistry.getInstance().unregister(username, session);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.log(Level.WARNING, "WebSocket error for user " + username, error);
    }

    private void sendError(Session session, String message) {
        ChatMessagePayload error = new ChatMessagePayload();
        error.setType("ERROR");
        error.setContent(message);
        try {
            session.getBasicRemote().sendObject(error);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to deliver error frame", e);
        }
    }

    private void closeQuietly(Session session, CloseReason.CloseCode code, String reason) {
        try {
            session.close(new CloseReason(code, reason));
        } catch (IOException ignored) {
            // Session is going away either way.
        }
    }
}
