<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="isEdit" value="${not empty user}" />
<c:set var="pageTitle" value="${isEdit ? 'Edit User Account' : 'Add New User'}" />
<c:set var="activeNav" value="users" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="row justify-content-center">
            <div class="col-12 col-md-8 col-lg-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-white py-3">
                        <div class="d-flex align-items-center gap-2">
                            <i class="bi ${isEdit ? 'bi-pencil-square' : 'bi-person-plus-fill'} text-primary fs-5"></i>
                            <h5 class="card-title mb-0 fw-bold">${isEdit ? 'Edit User Details' : 'Create New Account'}</h5>
                        </div>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/users/${isEdit ? 'edit' : 'create'}" method="post" class="needs-validation" novalidate>
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                            <c:if test="${isEdit}">
                                <input type="hidden" name="id" value="${user.id}" />
                            </c:if>

                            <div class="mb-3">
                                <label for="name" class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="name" name="name" 
                                       value="<c:out value='${isEdit ? user.name : name}' />" required minlength="2" maxlength="100">
                                <div class="invalid-feedback">Full name must be at least 2 characters.</div>
                            </div>

                            <div class="mb-3">
                                <label for="email" class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       value="<c:out value='${isEdit ? user.email : email}' />" required>
                                <div class="invalid-feedback">Please enter a valid unique email address.</div>
                            </div>

                            <c:if test="${!isEdit}">
                                <div class="mb-3">
                                    <label for="password" class="form-label fw-semibold">Initial Password <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" id="password" name="password" required minlength="6">
                                    <div class="form-text">Minimum 6 characters. Will be securely hashed using BCrypt.</div>
                                    <div class="invalid-feedback">Password must be at least 6 characters.</div>
                                </div>
                            </c:if>

                            <div class="row g-3 mb-4">
                                <div class="col-12 col-sm-6">
                                    <label for="role" class="form-label fw-semibold">System Role <span class="text-danger">*</span></label>
                                    <select class="form-select" id="role" name="role" required>
                                        <c:forEach var="r" items="${roles}">
                                            <option value="${r.name()}" ${(isEdit && user.role == r) || selectedRole == r.name() ? 'selected' : ''}>
                                                ${r.displayName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="col-12 col-sm-6">
                                    <label for="accountStatus" class="form-label fw-semibold">Account Status <span class="text-danger">*</span></label>
                                    <select class="form-select" id="accountStatus" name="accountStatus" required>
                                        <c:forEach var="s" items="${statuses}">
                                            <option value="${s.name()}" ${(isEdit && user.accountStatus == s) || selectedStatus == s.name() ? 'selected' : ''}>
                                                ${s.displayName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="d-flex justify-content-end gap-2 border-top pt-3">
                                <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">Cancel</a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check2-circle me-1"></i> ${isEdit ? 'Save Changes' : 'Create User'}
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
