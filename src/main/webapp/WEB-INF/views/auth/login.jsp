<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In | ProManage Enterprise</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet">
</head>
<body class="login-body">
    <div class="login-card">
        <div class="text-center mb-4">
            <div class="login-logo">
                <i class="bi bi-kanban-fill"></i>
            </div>
            <h2 class="fw-bold text-dark fs-3 mb-1">ProManage Enterprise</h2>
            <p class="text-secondary small">Online Project Management & Collaboration Platform</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show small d-flex align-items-center" role="alert">
                <i class="bi bi-exclamation-circle-fill me-2 fs-6"></i>
                <div><c:out value="${errorMessage}" /></div>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${param.logout == 'success'}">
            <div class="alert alert-success alert-dismissible fade show small d-flex align-items-center" role="alert">
                <i class="bi bi-check-circle-fill me-2 fs-6"></i>
                <div>You have been signed out successfully.</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" class="needs-validation" novalidate>
            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />

            <div class="mb-3">
                <label for="email" class="form-label fw-semibold small text-secondary">Email Address</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-envelope"></i></span>
                    <input type="email" class="form-control border-start-0 ps-0" id="email" name="email"
                           value="${not empty email ? email : ''}" placeholder="name@company.com" required autofocus>
                </div>
                <div class="invalid-feedback">Please enter a valid email address.</div>
            </div>

            <div class="mb-4">
                <div class="d-flex justify-content-between align-items-center mb-1">
                    <label for="password" class="form-label fw-semibold small text-secondary mb-0">Password</label>
                </div>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control border-start-0 ps-0" id="password" name="password"
                           placeholder="••••••••" required>
                </div>
                <div class="invalid-feedback">Please enter your password.</div>
            </div>

            <button type="submit" class="btn btn-primary w-100 py-2 fs-6 mb-3">
                <i class="bi bi-box-arrow-in-right me-2"></i> Sign In
            </button>
        </form>

        <!-- Quick Demo Credentials for Academic Review / Examiners -->
        <div class="border-top pt-3 mt-4">
            <div class="text-muted small fw-semibold text-center mb-2">
                <i class="bi bi-lightning-charge-fill text-warning me-1"></i> Quick Demo Access (1-Click Fill)
            </div>
            <div class="d-grid gap-2">
                <button type="button" class="btn btn-sm btn-outline-danger text-start px-2 py-1"
                        onclick="fillDemo('admin@promanage.com', 'Admin@123')">
                    <i class="bi bi-shield-lock-fill me-1"></i> <strong>Admin:</strong> admin@promanage.com
                </button>
                <button type="button" class="btn btn-sm btn-outline-primary text-start px-2 py-1"
                        onclick="fillDemo('sarah.pm@promanage.com', 'Manager@123')">
                    <i class="bi bi-person-workspace me-1"></i> <strong>Project Manager:</strong> sarah.pm@promanage.com
                </button>
                <button type="button" class="btn btn-sm btn-outline-info text-start px-2 py-1"
                        onclick="fillDemo('alex.dev@promanage.com', 'Member@123')">
                    <i class="bi bi-code-slash me-1"></i> <strong>Team Member:</strong> alex.dev@promanage.com
                </button>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function fillDemo(email, pass) {
            document.getElementById('email').value = email;
            document.getElementById('password').value = pass;
        }
    </script>
</body>
</html>
