package com.mzansi.chatzone.websocket;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * Paired with {@link MessageEncoder}. Only reads the fields a client is
 * ever trusted to send — {@code type}, {@code conversationId} and
 * {@code content}. Sender identity is never taken from this payload; see
 * {@link ChatEndpoint#onMessage}.
 */
public class MessageDecoder implements Decoder.Text<ChatMessagePayload> {

    @Override
    public ChatMessagePayload decode(String text) throws DecodeException {
        try {
            JSONObject json = new JSONObject(new JSONTokener(text));
            ChatMessagePayload payload = new ChatMessagePayload();
            payload.setType(json.optString("type", "TEXT"));
            payload.setConversationId(json.getInt("conversationId"));
            payload.setContent(json.optString("content", null));
            return payload;
        } catch (Exception e) {
            throw new DecodeException(text, "Malformed chat message frame", e);
        }
    }

    @Override
    public boolean willDecode(String text) {
        if (text == null) {
            return false;
        }
        try {
            new JSONObject(new JSONTokener(text));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
        // No setup required.
    }

    @Override
    public void destroy() {
        // No teardown required.
    }
}
