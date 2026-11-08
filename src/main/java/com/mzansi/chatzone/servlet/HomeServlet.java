package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.dao.ConversationDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The post-login landing page. Guarded by {@code AuthenticationFilter},
 * so by the time this runs a valid session is guaranteed to exist.
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final ConversationDao conversationDao = new ConversationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("userId");

        int conversationCount = conversationDao.listForUser(userId).size();
        request.setAttribute("conversationCount", conversationCount);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/home.jsp");
        dispatcher.forward(request, response);
    }
}
