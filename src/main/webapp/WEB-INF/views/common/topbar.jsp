<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="topbar">
    <div class="d-flex align-items-center gap-3">
        <button id="sidebar-toggle" class="btn btn-outline-secondary d-lg-none" type="button" aria-label="Toggle navigation">
            <i class="bi bi-list fs-5"></i>
        </button>
        <h1 class="topbar-title">${not empty pageTitle ? pageTitle : "Dashboard"}</h1>
    </div>

    <div class="d-flex align-items-center gap-3">
        <!-- Notification Bell -->
        <a href="${pageContext.request.contextPath}/notifications" class="btn btn-light position-relative p-2 rounded-circle" title="Notifications">
            <i class="bi bi-bell fs-5 text-secondary"></i>
            <span class="position-absolute top-0 start-100 translate-middle p-1 bg-danger border border-light rounded-circle">
                <span class="visually-hidden">New alerts</span>
            </span>
        </a>

        <!-- User Dropdown Menu -->
        <div class="dropdown">
            <button class="btn btn-light d-flex align-items-center gap-2 border-0 bg-transparent py-1 px-2" 
                    type="button" id="userMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
                <div class="avatar-circle" style="width: 34px; height: 34px; font-size: 0.85rem;">
                    ${sessionScope.currentUser.name.substring(0, 1)}
                </div>
                <div class="d-none d-md-block text-start">
                    <span class="d-block fw-semibold text-dark small lh-1">${sessionScope.currentUser.name}</span>
                    <small class="text-muted" style="font-size: 0.75rem;">${sessionScope.currentUser.role.displayName}</small>
                </div>
                <i class="bi bi-chevron-down text-muted small ms-1"></i>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2" aria-labelledby="userMenuButton">
                <li class="px-3 py-2 border-bottom">
                    <div class="fw-bold">${sessionScope.currentUser.name}</div>
                    <small class="text-muted">${sessionScope.currentUser.email}</small>
                </li>
                <li>
                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile">
                        <i class="bi bi-person me-2"></i> My Profile
                    </a>
                </li>
                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                    <li>
                        <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/settings">
                            <i class="bi bi-gear me-2"></i> Settings
                        </a>
                    </li>
                </c:if>
                <li><hr class="dropdown-divider"></li>
                <li>
                    <a class="dropdown-item py-2 text-danger" href="${pageContext.request.contextPath}/logout">
                        <i class="bi bi-box-arrow-right me-2"></i> Sign Out
                    </a>
                </li>
            </ul>
        </div>
    </div>
</header>
