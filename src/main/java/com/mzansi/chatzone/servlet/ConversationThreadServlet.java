package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.dao.ConversationDao;
import com.mzansi.chatzone.model.Message;
import com.mzansi.chatzone.model.User;
import com.mzansi.chatzone.service.ChatService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Renders one conversation thread: message history server-side, then
 * chat.js takes over via the WebSocket connection for anything that
 * arrives after page load. Access is checked on every request — a
 * conversation id in the URL is exactly the kind of thing a user could
 * type in by hand, so membership is never assumed.
 */
@WebServlet("/conversation")
public class ConversationThreadServlet extends HttpServlet {

    private final ConversationDao conversationDao = new ConversationDao();
    private final ChatService chatService = new ChatService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("userId");

        int conversationId;
        try {
            conversationId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/conversations");
            return;
        }

        if (!conversationDao.isParticipant(conversationId, userId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not part of this conversation.");
            return;
        }

        User otherUser = conversationDao.findOtherParticipant(conversationId, userId);
        List<Message> history = chatService.history(conversationId);

        request.setAttribute("conversationId", conversationId);
        request.setAttribute("otherUser", otherUser);
        request.setAttribute("messages", history);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/conversation.jsp");
        dispatcher.forward(request, response);
    }
}
