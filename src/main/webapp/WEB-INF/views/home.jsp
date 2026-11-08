<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mzansi.chatzone.util.HtmlEscaper" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Mzansi Chat Zone — Home</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<body>
<div class="shell">
    <div class="home-hero">
        <div class="greeting">Hi, <%= HtmlEscaper.escape((String) session.getAttribute("displayName")) %></div>
        <div class="subline">
            <%
                Object countObj = request.getAttribute("conversationCount");
                int count = countObj == null ? 0 : (Integer) countObj;
                if (count == 0) {
            %>
            You don't have any conversations yet — start one below.
            <% } else if (count == 1) { %>
            You have 1 conversation waiting.
            <% } else { %>
            You have <%= count %> conversations waiting.
            <% } %>
        </div>
    </div>

    <div class="home-nav">
        <a class="home-nav-card" href="${pageContext.request.contextPath}/conversations">
            <span class="nav-icon" aria-hidden="true">&#128172;</span>
            <span class="nav-copy">
                Conversations
                <div class="nav-meta">Chat with people in real time</div>
            </span>
        </a>
        <a class="home-nav-card" href="${pageContext.request.contextPath}/camera">
            <span class="nav-icon" aria-hidden="true">&#128247;</span>
            <span class="nav-copy">
                Camera
                <div class="nav-meta">Capture and share a photo</div>
            </span>
        </a>
    </div>

    <div class="home-logout">
        <a href="${pageContext.request.contextPath}/logout">Log out</a>
    </div>
</div>
</body>
</html>
