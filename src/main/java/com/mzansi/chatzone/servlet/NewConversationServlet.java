package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.dao.ConversationDao;
import com.mzansi.chatzone.dao.UserDao;
import com.mzansi.chatzone.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles the "New Chat" form on the conversations page. Doubles as the
 * app's "contacts" feature: rather than maintain a separate contacts
 * list, starting a chat with any registered username is the contact
 * lookup, which is all the brief actually calls for.
 */
@WebServlet("/new-conversation")
public class NewConversationServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final ConversationDao conversationDao = new ConversationDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int myUserId = (Integer) session.getAttribute("userId");
        String myUsername = (String) session.getAttribute("username");

        String targetUsername = request.getParameter("username");
        targetUsername = targetUsername == null ? "" : targetUsername.trim();

        if (targetUsername.isEmpty()) {
            redirectWithError(request, response, "Enter a username to start a chat.");
            return;
        }
        if (targetUsername.equalsIgnoreCase(myUsername)) {
            redirectWithError(request, response, "You can't start a chat with yourself.");
            return;
        }

        User target = userDao.findByUsername(targetUsername);
        if (target == null) {
            redirectWithError(request, response, "No user found with username \"" + targetUsername + "\".");
            return;
        }

        int conversationId = conversationDao.findOrCreateOneToOne(myUserId, target.getId());
        response.sendRedirect(request.getContextPath() + "/conversation?id=" + conversationId);
    }

    private void redirectWithError(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/conversations?error=" + encoded);
    }
}
