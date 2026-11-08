package com.mzansi.chatzone.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Standard JSR 356 pattern for tying a WebSocket connection to the HTTP
 * session it arrived on. Without this, {@code @ServerEndpoint} has no
 * built-in notion of "who is this" — a raw WebSocket handshake carries
 * no authentication of its own.
 *
 * <p>The browser's WebSocket handshake is same-origin, so it carries the
 * JSESSIONID cookie automatically; this configurator copies the resulting
 * {@link HttpSession} into the endpoint's user properties, where
 * {@link ChatEndpoint#onOpen} reads back the {@code username} that
 * {@code LoginServlet} put there at login time. A handshake with no
 * logged-in session (no "username" attribute) is rejected in
 * {@code onOpen}, not here — {@code modifyHandshake} only has access to
 * copy state, not to reject the upgrade.</p>
 */
public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        Object httpSession = request.getHttpSession();
        if (httpSession instanceof HttpSession) {
            config.getUserProperties().put("httpSession", httpSession);
        }
    }
}
