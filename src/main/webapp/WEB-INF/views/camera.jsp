<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mzansi.chatzone.util.HtmlEscaper" %>
<%@ page import="com.mzansi.chatzone.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Mzansi Chat Zone — Camera</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<%
    Integer conversationId = (Integer) request.getAttribute("conversationId");
    User otherUser = (User) request.getAttribute("otherUser");
    String err = request.getParameter("error");
%>
<% if (conversationId == null) { %>
<body>
<div class="shell">
    <div class="app-header">
        <a class="back-link" href="${pageContext.request.contextPath}/home">&larr; Home</a>
        <h1>Camera</h1>
        <span style="width:24px"></span>
    </div>
    <div class="empty-state">
        <div class="empty-title">Pick a conversation first</div>
        <p>Photos are sent into a specific chat — open one from your conversations, then tap the camera icon.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/conversations">Go to conversations</a>
    </div>
</div>
</body>
<% } else { %>
<body data-context-path="${pageContext.request.contextPath}" data-conversation-id="<%= conversationId %>">
<div class="shell" style="min-height:100vh;">
    <div class="app-header">
        <a class="back-link" href="${pageContext.request.contextPath}/conversation?id=<%= conversationId %>">&larr; Back</a>
        <h1>Camera</h1>
        <span style="width:24px"></span>
    </div>

    <% if (err != null) { %>
    <div class="form-error container-narrow"><%= HtmlEscaper.escape(err) %></div>
    <% } %>

    <div class="camera-stage">
        <div class="camera-viewport">
            <video id="cameraVideo" autoplay playsinline muted></video>
            <img id="capturedPreview" style="display:none;" alt="Captured photo">
            <div id="cameraHint" class="hint" style="display:none;">Camera unavailable</div>
        </div>
        <div id="cameraError" class="form-error" style="display:none; margin-top:14px;"></div>
        <div class="camera-controls">
            <button id="shutterBtn" class="shutter-btn" aria-label="Take photo"></button>
            <button id="retakeBtn" class="btn btn-secondary" style="display:none;">Retake</button>
            <button id="sendBtn" class="btn btn-primary" style="display:none;">Send</button>
        </div>
        <div class="camera-context">
            <% if (otherUser != null) { %>
            Sending to <%= HtmlEscaper.escape(otherUser.getDisplayName()) %>
            <% } %>
        </div>
    </div>
    <canvas id="captureCanvas" style="display:none;"></canvas>
</div>
<script src="${pageContext.request.contextPath}/js/camera.js"></script>
</body>
<% } %>
</html>
