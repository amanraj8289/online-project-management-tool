<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside id="sidebar">
    <!-- Brand Logo -->
    <div class="sidebar-brand">
        <div class="brand-icon">
            <i class="bi bi-kanban-fill"></i>
        </div>
        <div class="brand-text">ProManage</div>
    </div>

    <!-- Navigation Menu -->
    <ul class="sidebar-menu">
        <c:choose>
            <%-- ==================== ADMIN MENU ==================== --%>
            <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
                <li class="sidebar-heading">Administration</li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="sidebar-link ${activeNav == 'dashboard' ? 'active' : ''}">
                        <i class="bi bi-grid-1x2-fill"></i>
                        <span>Admin Dashboard</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/users" class="sidebar-link ${activeNav == 'users' ? 'active' : ''}">
                        <i class="bi bi-people-fill"></i>
                        <span>User Accounts</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/projects" class="sidebar-link ${activeNav == 'projects' ? 'active' : ''}">
                        <i class="bi bi-folder-fill"></i>
                        <span>Projects</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/tasks" class="sidebar-link ${activeNav == 'tasks' ? 'active' : ''}">
                        <i class="bi bi-check2-square"></i>
                        <span>All Tasks</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/settings" class="sidebar-link ${activeNav == 'settings' ? 'active' : ''}">
                        <i class="bi bi-sliders"></i>
                        <span>System Settings</span>
                    </a>
                </li>
            </c:when>

            <%-- ==================== PROJECT MANAGER MENU ==================== --%>
            <c:when test="${sessionScope.currentUser.role == 'PROJECT_MANAGER'}">
                <li class="sidebar-heading">Management</li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/manager/dashboard" class="sidebar-link ${activeNav == 'dashboard' ? 'active' : ''}">
                        <i class="bi bi-speedometer2"></i>
                        <span>PM Dashboard</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/projects" class="sidebar-link ${activeNav == 'projects' ? 'active' : ''}">
                        <i class="bi bi-folder2-open"></i>
                        <span>My Projects</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/tasks" class="sidebar-link ${activeNav == 'tasks' ? 'active' : ''}">
                        <i class="bi bi-list-task"></i>
                        <span>Task Management</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/manager/reports" class="sidebar-link ${activeNav == 'reports' ? 'active' : ''}">
                        <i class="bi bi-graph-up-arrow"></i>
                        <span>Progress Reports</span>
                    </a>
                </li>
            </c:when>

            <%-- ==================== TEAM MEMBER MENU ==================== --%>
            <c:otherwise>
                <li class="sidebar-heading">Workspace</li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/member/dashboard" class="sidebar-link ${activeNav == 'dashboard' ? 'active' : ''}">
                        <i class="bi bi-house-door-fill"></i>
                        <span>My Dashboard</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/member/my-tasks" class="sidebar-link ${activeNav == 'my-tasks' ? 'active' : ''}">
                        <i class="bi bi-check-circle-fill"></i>
                        <span>My Tasks</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/projects" class="sidebar-link ${activeNav == 'projects' ? 'active' : ''}">
                        <i class="bi bi-folder-symlink-fill"></i>
                        <span>My Projects</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/member/activity" class="sidebar-link ${activeNav == 'activity' ? 'active' : ''}">
                        <i class="bi bi-clock-history"></i>
                        <span>Activity Feed</span>
                    </a>
                </li>
            </c:otherwise>
        </c:choose>

        <li class="sidebar-heading mt-3">Account</li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/notifications" class="sidebar-link ${activeNav == 'notifications' ? 'active' : ''}">
                <i class="bi bi-bell-fill"></i>
                <span>Notifications</span>
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/profile" class="sidebar-link ${activeNav == 'profile' ? 'active' : ''}">
                <i class="bi bi-person-circle"></i>
                <span>My Profile</span>
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/logout" class="sidebar-link text-danger-emphasis">
                <i class="bi bi-box-arrow-right text-danger"></i>
                <span>Sign Out</span>
            </a>
        </li>
    </ul>

    <!-- Sidebar Bottom User Profile snippet -->
    <div class="sidebar-user">
        <div class="avatar-circle">
            ${sessionScope.currentUser.name.substring(0, 1)}
        </div>
        <div class="overflow-hidden">
            <div class="text-white text-truncate fw-semibold small">${sessionScope.currentUser.name}</div>
            <span class="${sessionScope.currentUser.role.badgeClass}" style="font-size: 0.65rem;">
                ${sessionScope.currentUser.role.displayName}
            </span>
        </div>
    </div>
</aside>
