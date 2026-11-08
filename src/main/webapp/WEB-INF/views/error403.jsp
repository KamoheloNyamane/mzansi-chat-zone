<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Access denied — Mzansi Chat Zone</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<body>
<div class="shell shell--centered">
    <div class="empty-state">
        <div class="empty-title">You don't have access to that</div>
        <p>This conversation belongs to someone else's account.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/conversations">Back to conversations</a>
    </div>
</div>
</body>
</html>
