<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Page not found — Mzansi Chat Zone</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<body>
<div class="shell shell--centered">
    <div class="empty-state">
        <div class="empty-title">That page doesn't exist</div>
        <p>The link may be old, or the address may be mistyped.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Go home</a>
    </div>
</div>
</body>
</html>
