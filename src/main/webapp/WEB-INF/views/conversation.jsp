<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mzansi.chatzone.util.HtmlEscaper" %>
<%@ page import="com.mzansi.chatzone.model.Message" %>
<%@ page import="com.mzansi.chatzone.model.MessageType" %>
<%@ page import="com.mzansi.chatzone.model.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Mzansi Chat Zone — Conversation</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<%
    User otherUser = (User) request.getAttribute("otherUser");
    Integer conversationId = (Integer) request.getAttribute("conversationId");
    String otherDisplayName = otherUser != null ? otherUser.getDisplayName() : "Unknown user";
    String otherInitial = otherDisplayName.isEmpty() ? "?" : otherDisplayName.substring(0, 1).toUpperCase();
    String currentUsername = (String) session.getAttribute("username");
%>
<body data-context-path="${pageContext.request.contextPath}"
      data-conversation-id="<%= conversationId %>"
      data-username="<%= HtmlEscaper.escape(currentUsername) %>">
<div class="shell" style="min-height:100vh;">
    <div class="thread-header">
        <a class="icon-btn" href="${pageContext.request.contextPath}/conversations" aria-label="Back to conversations">&larr;</a>
        <span class="avatar"><%= HtmlEscaper.escape(otherInitial) %></span>
        <span class="name"><%= HtmlEscaper.escape(otherDisplayName) %></span>
    </div>

    <div class="connection-pill" id="connectionPill">Connecting…</div>
    <div class="thread-error" id="threadError" style="display:none;"></div>

    <div class="message-list" id="messageList">
        <%
            @SuppressWarnings("unchecked")
            List<Message> messages = (List<Message>) request.getAttribute("messages");
            if (messages != null) {
                for (Message m : messages) {
                    boolean mine = m.getSenderUsername() != null && m.getSenderUsername().equals(currentUsername);
                    String timeLabel = m.getSentAt() != null
                            ? m.getSentAt().format(DateTimeFormatter.ofPattern("h:mm a")) : "";
        %>
        <div class="bubble-row <%= mine ? "mine" : "theirs" %>">
            <div class="bubble">
                <% if (m.getType() == MessageType.IMAGE && m.getImagePath() != null) { %>
                <img src="${pageContext.request.contextPath}/<%= m.getImagePath() %>" alt="Shared photo">
                <% } else { %>
                <span><%= HtmlEscaper.escape(m.getContent()) %></span>
                <% } %>
                <span class="bubble-time"><%= timeLabel %></span>
            </div>
        </div>
        <% } } %>
    </div>

    <div class="composer">
        <a class="camera-link" href="${pageContext.request.contextPath}/camera?conversationId=<%= conversationId %>" aria-label="Open camera">&#128247;</a>
        <form id="composerForm" autocomplete="off">
            <input type="text" id="messageInput" name="message" placeholder="Type a message…" maxlength="2000" autocomplete="off">
            <button type="submit" class="send-btn" aria-label="Send">&#10148;</button>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/chat.js"></script>
</body>
</html>
