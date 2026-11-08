package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.model.Message;
import com.mzansi.chatzone.service.ChatService;
import com.mzansi.chatzone.util.ImageStorageUtil;
import com.mzansi.chatzone.websocket.SessionRegistry;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The HTTP side of image sharing: the browser posts the still captured
 * on the camera page here as multipart form data. This servlet persists
 * it exactly like a text message and pushes it out through
 * {@link SessionRegistry#broadcast} — the same delivery path
 * {@code ChatEndpoint} uses for text — so every open client sees the
 * photo appear in real time, even though it never touched the WebSocket
 * connection to get there.
 */
@WebServlet("/upload-image")
@MultipartConfig(maxFileSize = 8 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class ImageUploadServlet extends HttpServlet {

    private final ChatService chatService = new ChatService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("userId");

        int conversationId;
        try {
            conversationId = Integer.parseInt(request.getParameter("conversationId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid conversationId.");
            return;
        }

        if (!chatService.isParticipant(conversationId, userId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not part of this conversation.");
            return;
        }

        Part photoPart = request.getPart("photo");
        if (photoPart == null || photoPart.getSize() == 0) {
            redirectWithError(request, response, conversationId, "No photo was captured.");
            return;
        }

        String uploadsRealPath = getServletContext().getRealPath("/uploads");
        String imagePath;
        try {
            imagePath = ImageStorageUtil.store(photoPart, uploadsRealPath);
        } catch (IllegalArgumentException e) {
            redirectWithError(request, response, conversationId, e.getMessage());
            return;
        }

        Message saved = chatService.sendImage(conversationId, userId, imagePath);
        SessionRegistry.getInstance().broadcast(chatService.toPayload(saved));

        response.sendRedirect(request.getContextPath() + "/conversation?id=" + conversationId);
    }

    private void redirectWithError(HttpServletRequest request, HttpServletResponse response,
                                    int conversationId, String message) throws IOException {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/camera?conversationId=" + conversationId + "&error=" + encoded);
    }
}
