package com.mzansi.chatzone.servlet;

import com.mzansi.chatzone.service.AuthService;
import com.mzansi.chatzone.service.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GET renders the registration form; POST creates the account. On a
 * validation failure the form is re-shown (not redirected) with the
 * error and the username/display name the visitor already typed — the
 * password is never echoed back, on success or failure.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String displayName = request.getParameter("displayName");
        String password = request.getParameter("password");

        try {
            authService.register(username, displayName, password == null ? new char[0] : password.toCharArray());
            response.sendRedirect(request.getContextPath() + "/login?registered=1");
        } catch (ValidationException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            request.setAttribute("displayName", displayName);
            forwardToForm(request, response);
        }
    }

    private void forwardToForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/register.jsp");
        dispatcher.forward(request, response);
    }
}
