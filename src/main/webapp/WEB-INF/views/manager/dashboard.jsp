<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Project Manager Dashboard" />
<c:set var="activeNav" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <!-- Header Actions -->
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
            <div>
                <h4 class="fw-bold mb-1">Welcome back, ${sessionScope.currentUser.name}</h4>
                <p class="text-secondary small mb-0">Monitor your project milestones, team velocity, and task deadlines.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/projects/create" class="btn btn-outline-primary btn-sm">
                    <i class="bi bi-folder-plus me-1"></i> New Project
                </a>
                <a href="${pageContext.request.contextPath}/tasks/create" class="btn btn-primary btn-sm">
                    <i class="bi bi-plus-lg me-1"></i> Assign Task
                </a>
            </div>
        </div>

        <!-- Metric Cards -->
        <div class="row g-3 mb-4">
            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card primary">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">My Projects</div>
                            <div class="metric-value">${metrics.totalProjects}</div>
                        </div>
                        <div class="metric-icon-box bg-primary-subtle text-primary">
                            <i class="bi bi-briefcase-fill"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">${metrics.activeProjects} currently active</div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card info">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">Total Tasks</div>
                            <div class="metric-value">${metrics.totalTasks}</div>
                        </div>
                        <div class="metric-icon-box bg-info-subtle text-info">
                            <i class="bi bi-card-checklist"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">${metrics.inProgressTasks} currently in progress</div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card success">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">Completed Tasks</div>
                            <div class="metric-value">${metrics.completedTasks}</div>
                        </div>
                        <div class="metric-icon-box bg-success-subtle text-success">
                            <i class="bi bi-check2-all"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">${metrics.overallCompletionRate}% completion rate</div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card danger">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">Overdue Tasks</div>
                            <div class="metric-value">${metrics.overdueTasks}</div>
                        </div>
                        <div class="metric-icon-box bg-danger-subtle text-danger">
                            <i class="bi bi-alarm-fill"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-danger fw-semibold">Requires attention</div>
                </div>
            </div>
        </div>

        <!-- Managed Projects Overview -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-folder2-open text-primary me-2"></i> Active Projects Timeline & Progress</span>
                <a href="${pageContext.request.contextPath}/projects" class="btn btn-sm btn-outline-primary">Manage All</a>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Project</th>
                            <th>Status</th>
                            <th>Deadline</th>
                            <th>Team Members</th>
                            <th>Task Progress</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${projects}">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="fw-bold text-decoration-none text-dark">
                                        <c:out value="${p.title}" />
                                    </a>
                                </td>
                                <td>
                                    <span class="${p.status.badgeClass}">${p.status.displayName}</span>
                                    <c:if test="${p.overdue}">
                                        <span class="badge bg-danger ms-1">Overdue</span>
                                    </c:if>
                                </td>
                                <td class="small text-muted">
                                    <fmt:formatDate value="${p.dueDate}" pattern="yyyy-MM-dd" />
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        <i class="bi bi-people-fill me-1 text-secondary"></i> ${p.teamMemberCount} Members
                                    </span>
                                </td>
                                <td style="min-width: 150px;">
                                    <div class="d-flex justify-content-between small text-muted mb-1">
                                        <span>${p.completedTasks}/${p.totalTasks}</span>
                                        <span class="fw-bold">${p.progressPercentage}%</span>
                                    </div>
                                    <div class="progress" style="height: 6px;">
                                        <div class="progress-bar bg-primary" role="progressbar" style="width: ${p.progressPercentage}%;"></div>
                                    </div>
                                </td>
                                <td class="text-end">
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="btn btn-outline-primary" title="View Project Details">
                                            <i class="bi bi-eye-fill"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/projects/edit?id=${p.id}" class="btn btn-outline-secondary" title="Edit Project">
                                            <i class="bi bi-pencil-fill"></i>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Managed Tasks Table -->
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-list-task text-primary me-2"></i> Tasks Across Projects</span>
                <a href="${pageContext.request.contextPath}/tasks/create" class="btn btn-sm btn-primary">
                    <i class="bi bi-plus-lg me-1"></i> New Task
                </a>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Task Title</th>
                            <th>Project</th>
                            <th>Assigned To</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Due Date</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="t" items="${tasks}">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="fw-bold text-decoration-none text-dark">
                                        <c:out value="${t.title}" />
                                    </a>
                                </td>
                                <td class="text-secondary small"><c:out value="${t.projectTitle}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty t.assignedToName}">
                                            <span class="badge bg-light text-dark border">
                                                <i class="bi bi-person-fill text-primary me-1"></i> <c:out value="${t.assignedToName}" />
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary-subtle text-secondary">Unassigned</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="${t.priority.badgeClass}">${t.priority.displayName}</span></td>
                                <td><span class="${t.status.badgeClass}">${t.status.displayName}</span></td>
                                <td class="small ${t.overdue ? 'text-danger fw-bold' : 'text-muted'}">
                                    <fmt:formatDate value="${t.dueDate}" pattern="yyyy-MM-dd" />
                                    <c:if test="${t.overdue}">
                                        <i class="bi bi-exclamation-circle ms-1" title="Overdue"></i>
                                    </c:if>
                                </td>
                                <td class="text-end">
                                    <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="btn btn-sm btn-outline-primary">
                                        <i class="bi bi-eye-fill"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
