package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.dao.ConversationDao;
import com.mzansi.chatzone.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Serves the camera page. A conversation id is required — the brief's
 * camera functionality is for "capturing and sharing" a photo, which
 * only makes sense in the context of a specific chat to send it into.
 */
@WebServlet("/camera")
public class CameraServlet extends HttpServlet {

    private final ConversationDao conversationDao = new ConversationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("userId");

        String idParam = request.getParameter("conversationId");
        Integer conversationId = null;
        User otherUser = null;

        if (idParam != null) {
            try {
                int candidate = Integer.parseInt(idParam);
                if (conversationDao.isParticipant(candidate, userId)) {
                    conversationId = candidate;
                    otherUser = conversationDao.findOtherParticipant(candidate, userId);
                }
            } catch (NumberFormatException ignored) {
                // Falls through with conversationId left null — camera.jsp shows a "pick a chat first" state.
            }
        }

        request.setAttribute("conversationId", conversationId);
        request.setAttribute("otherUser", otherUser);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/camera.jsp");
        dispatcher.forward(request, response);
    }
}
