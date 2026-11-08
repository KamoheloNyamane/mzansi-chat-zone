package com.mzansi.chatzone.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Guards every page a visitor should only reach once logged in. Applied
 * by URL pattern rather than duplicated inside each servlet — "is there
 * a valid session" is a cross-cutting concern, not business logic any
 * one page owns.
 */
@WebFilter(urlPatterns = {
        "/home",
        "/conversations",
        "/conversation",
        "/new-conversation",
        "/camera",
        "/upload-image",
        "/logout"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization required.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute("username") != null;

        if (!authenticated) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // No teardown required.
    }
}
