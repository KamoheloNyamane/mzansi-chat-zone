<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Something went wrong — Mzansi Chat Zone</title>
    <%@ include file="/WEB-INF/views/partials/head.jspf" %>
</head>
<body>
<div class="shell shell--centered">
    <div class="empty-state">
        <div class="empty-title">Something went wrong on our end</div>
        <p>Try again in a moment. If this keeps happening, check that the database is running.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Go home</a>
    </div>
</div>
</body>
</html>
