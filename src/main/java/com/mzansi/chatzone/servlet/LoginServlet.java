package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.model.User;
import com.mzansi.chatzone.service.AuthService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * GET renders the login form; POST authenticates and establishes the
 * session. The three session attributes set here on success —
 * {@code username}, {@code userId}, {@code displayName} — are the
 * contract the rest of the app relies on: {@code AuthenticationFilter}
 * checks for {@code username}, and {@code ChatEndpoint} reads
 * {@code username}/{@code userId} back out during the WebSocket handshake.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession existing = request.getSession(false);
        if (existing != null && existing.getAttribute("username") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        forwardToForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = authService.authenticate(username, password == null ? new char[0] : password.toCharArray());
        if (user == null) {
            request.setAttribute("error", "Incorrect username or password.");
            request.setAttribute("username", username);
            forwardToForm(request, response);
            return;
        }

        // A fresh session on every successful login prevents session fixation.
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userId", user.getId());
        session.setAttribute("displayName", user.getDisplayName());

        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void forwardToForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(request, response);
    }
}
