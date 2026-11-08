package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.dao.ConversationDao;
import com.mzansi.chatzone.model.ConversationSummary;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/conversations")
public class ConversationsServlet extends HttpServlet {

    private final ConversationDao conversationDao = new ConversationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("userId");

        List<ConversationSummary> conversations = conversationDao.listForUser(userId);
        request.setAttribute("conversations", conversations);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/conversations.jsp");
        dispatcher.forward(request, response);
    }
}
