<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="User Profile" />
<c:set var="activeNav" value="profile" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="row g-4">
            <!-- Left Column: Personal Information -->
            <div class="col-12 col-lg-6">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-person-lines-fill text-primary me-2"></i> Account Profile Details</span>
                    </div>
                    <div class="card-body p-4">
                        <div class="d-flex align-items-center gap-3 mb-4 pb-3 border-bottom">
                            <div class="avatar-circle" style="width: 54px; height: 54px; font-size: 1.5rem;">
                                ${user.name.substring(0, 1)}
                            </div>
                            <div>
                                <h5 class="fw-bold mb-0"><c:out value="${user.name}" /></h5>
                                <span class="${user.role.badgeClass} mt-1">${user.role.displayName}</span>
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/profile/update" method="post" class="needs-validation" novalidate>
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />

                            <div class="mb-3">
                                <label for="name" class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="name" name="name"
                                       value="<c:out value='${user.name}' />" required minlength="2" maxlength="100">
                            </div>

                            <div class="mb-3">
                                <label for="email" class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="<c:out value='${user.email}' />" required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold text-muted">Role</label>
                                <input type="text" class="form-control bg-light" value="${user.role.displayName}" disabled>
                            </div>

                            <div class="mb-4">
                                <label class="form-label fw-semibold text-muted">Account Registered</label>
                                <input type="text" class="form-control bg-light" 
                                       value="<fmt:formatDate value='${user.createdAt}' pattern='yyyy-MM-dd HH:mm:ss' />" disabled>
                            </div>

                            <div class="text-end">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-save me-1"></i> Update Profile
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Right Column: Change Password -->
            <div class="col-12 col-lg-6">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-shield-lock-fill text-primary me-2"></i> Change Account Password</span>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/profile/password" method="post" class="needs-validation" novalidate>
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />

                            <div class="mb-3">
                                <label for="currentPassword" class="form-label fw-semibold">Current Password <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                                <div class="invalid-feedback">Current password is required.</div>
                            </div>

                            <div class="mb-3">
                                <label for="newPassword" class="form-label fw-semibold">New Password <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="newPassword" name="newPassword" required minlength="6">
                                <div class="form-text">Must be at least 6 characters long.</div>
                                <div class="invalid-feedback">Password must be at least 6 characters.</div>
                            </div>

                            <div class="mb-4">
                                <label for="confirmPassword" class="form-label fw-semibold">Confirm New Password <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required minlength="6">
                                <div class="invalid-feedback">Please confirm your new password.</div>
                            </div>

                            <div class="text-end">
                                <button type="submit" class="btn btn-danger">
                                    <i class="bi bi-key-fill me-1"></i> Change Password
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
