<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mzansi.chatzone.util.HtmlEscaper" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Mzansi Chat Zone — Log in</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<body>
<div class="shell shell--centered">
    <div class="auth-brand">
        <div class="wordmark">Mzansi Chat Zone</div>
        <div class="tagline">Real conversations, right now.</div>
    </div>

    <div class="auth-card">
        <% if (request.getParameter("registered") != null) { %>
        <div class="form-success">Account created — log in to get started.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
        <div class="form-error"><%= HtmlEscaper.escape((String) request.getAttribute("error")) %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="field">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" autocomplete="username" required
                       value="<%= request.getAttribute("username") != null ? HtmlEscaper.escape((String) request.getAttribute("username")) : "" %>">
            </div>
            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" autocomplete="current-password" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Login</button>
        </form>
    </div>

    <div class="auth-switch">
        New here? <a href="${pageContext.request.contextPath}/register">Create an account</a>
    </div>
</div>
</body>
</html>
