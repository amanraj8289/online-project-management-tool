<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 - Internal Server Error | ProManage</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet">
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100 bg-light">
    <div class="text-center p-4" style="max-width: 550px;">
        <i class="bi bi-exclamation-octagon text-danger display-1 mb-3 d-block"></i>
        <h1 class="fw-bold display-5 text-dark">500 Server Error</h1>
        <p class="text-secondary lead mb-3">
            An unexpected error occurred while processing your request. The system administrator has been notified.
        </p>
        <c:if test="${not empty pageContext.exception}">
            <div class="alert alert-warning text-start small mb-4 font-monospace">
                <strong>Error:</strong> <c:out value="${pageContext.exception.message}" />
            </div>
        </c:if>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary px-4 py-2">
            <i class="bi bi-house-door-fill me-1"></i> Return to Dashboard
        </a>
    </div>
</body>
</html>
