package com.mzansi.chatzone.websocket;

import org.json.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Registered on {@link ChatEndpoint} so the endpoint can return/send a
 * {@link ChatMessagePayload} directly instead of hand-building JSON
 * strings inline — keeps the wire format defined in exactly one place
 * (paired with {@link MessageDecoder}).
 */
public class MessageEncoder implements Encoder.Text<ChatMessagePayload> {

    @Override
    public String encode(ChatMessagePayload payload) throws EncodeException {
        JSONObject json = new JSONObject();
        json.put("type", payload.getType());
        json.put("conversationId", payload.getConversationId());
        json.put("messageId", payload.getMessageId());
        json.put("senderUsername", payload.getSenderUsername());
        json.put("senderDisplayName", payload.getSenderDisplayName());
        json.putOpt("content", payload.getContent());
        json.putOpt("imagePath", payload.getImagePath());
        json.putOpt("sentAt", payload.getSentAt());
        return json.toString();
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
