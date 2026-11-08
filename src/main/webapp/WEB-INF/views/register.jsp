<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mzansi.chatzone.util.HtmlEscaper" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Mzansi Chat Zone — Create account</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<body>
<div class="shell shell--centered">
    <div class="auth-brand">
        <div class="wordmark">Mzansi Chat Zone</div>
        <div class="tagline">Create an account to start chatting.</div>
    </div>

    <div class="auth-card">
        <% if (request.getAttribute("error") != null) { %>
        <div class="form-error"><%= HtmlEscaper.escape((String) request.getAttribute("error")) %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <div class="field">
                <label for="displayName">Display name</label>
                <input type="text" id="displayName" name="displayName" autocomplete="name" required
                       value="<%= request.getAttribute("displayName") != null ? HtmlEscaper.escape((String) request.getAttribute("displayName")) : "" %>">
            </div>
            <div class="field">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" autocomplete="username" required
                       pattern="[a-zA-Z0-9._-]{3,50}" title="3-50 characters: letters, numbers, dots, underscores or hyphens"
                       value="<%= request.getAttribute("username") != null ? HtmlEscaper.escape((String) request.getAttribute("username")) : "" %>">
            </div>
            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" autocomplete="new-password" required minlength="8">
            </div>
            <button type="submit" class="btn btn-primary btn-block">Register</button>
        </form>
    </div>

    <div class="auth-switch">
        Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a>
    </div>
</div>
</body>
</html>
