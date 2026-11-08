<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mzansi.chatzone.util.HtmlEscaper" %>
<%@ page import="com.mzansi.chatzone.model.ConversationSummary" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Mzansi Chat Zone — Conversations</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<body>
<div class="shell">
    <div class="app-header">
        <a class="back-link" href="${pageContext.request.contextPath}/home">&larr; Home</a>
        <h1>Conversations</h1>
        <span style="width:24px"></span>
    </div>

    <% String err = request.getParameter("error");
       if (err != null) { %>
    <div class="form-error container-narrow"><%= HtmlEscaper.escape(err) %></div>
    <% } %>

    <div class="new-chat-bar">
        <form method="post" action="${pageContext.request.contextPath}/new-conversation">
            <input type="text" name="username" placeholder="Start a chat — enter a username" required>
            <button type="submit" class="btn btn-primary">Chat</button>
        </form>
    </div>

    <%
        @SuppressWarnings("unchecked")
        List<ConversationSummary> conversations = (List<ConversationSummary>) request.getAttribute("conversations");
        if (conversations == null || conversations.isEmpty()) {
    %>
    <div class="empty-state">
        <div class="empty-title">No conversations yet</div>
        <p>Enter a friend's username above to start your first chat.</p>
    </div>
    <% } else { %>
    <ul class="conversation-list">
        <% for (ConversationSummary c : conversations) {
            String initial = c.getOtherDisplayName() != null && !c.getOtherDisplayName().isEmpty()
                    ? c.getOtherDisplayName().substring(0, 1).toUpperCase() : "?";
            String preview = c.getLastMessagePreview() != null ? c.getLastMessagePreview() : "No messages yet";
            String timeLabel = "";
            if (c.getLastMessageAt() != null) {
                LocalDate msgDate = c.getLastMessageAt().toLocalDate();
                if (msgDate.equals(LocalDate.now())) {
                    timeLabel = c.getLastMessageAt().format(DateTimeFormatter.ofPattern("h:mm a"));
                } else {
                    timeLabel = msgDate.format(DateTimeFormatter.ofPattern("d MMM"));
                }
            }
        %>
        <li>
            <a class="conversation-row" href="${pageContext.request.contextPath}/conversation?id=<%= c.getConversationId() %>">
                <span class="avatar"><%= HtmlEscaper.escape(initial) %></span>
                <span class="conversation-info">
                    <div class="conversation-name"><%= HtmlEscaper.escape(c.getOtherDisplayName()) %></div>
                    <div class="conversation-preview"><%= HtmlEscaper.escape(preview) %></div>
                </span>
                <span class="conversation-time"><%= timeLabel %></span>
            </a>
        </li>
        <% } %>
    </ul>
    <% } %>
</div>
</body>
</html>
