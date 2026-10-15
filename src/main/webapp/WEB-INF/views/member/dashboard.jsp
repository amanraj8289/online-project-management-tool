<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Team Member Dashboard" />
<c:set var="activeNav" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="mb-4">
            <h4 class="fw-bold mb-1">Hello, ${sessionScope.currentUser.name}</h4>
            <p class="text-secondary small mb-0">Here is an overview of your active tasks and project deliverables.</p>
        </div>

        <!-- Metric Cards -->
        <div class="row g-3 mb-4">
            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card primary">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">Assigned Tasks</div>
                            <div class="metric-value">${metrics.totalTasks}</div>
                        </div>
                        <div class="metric-icon-box bg-primary-subtle text-primary">
                            <i class="bi bi-list-check"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">Across ${metrics.totalProjects} projects</div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card warning">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">Pending / To Do</div>
                            <div class="metric-value">${metrics.pendingTasks}</div>
                        </div>
                        <div class="metric-icon-box bg-warning-subtle text-warning">
                            <i class="bi bi-clock-fill"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">Waiting on progress</div>
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
                            <i class="bi bi-check2-circle"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">Delivered deliverables</div>
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
                            <i class="bi bi-exclamation-octagon-fill"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-danger fw-semibold">Requires immediate focus</div>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- My Current Tasks -->
            <div class="col-12 col-lg-8">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-card-checklist text-primary me-2"></i> My Current Deliverables</span>
                        <a href="${pageContext.request.contextPath}/member/my-tasks" class="btn btn-sm btn-outline-primary">View All</a>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Task</th>
                                    <th>Project</th>
                                    <th>Priority</th>
                                    <th>Status</th>
                                    <th>Due Date</th>
                                    <th class="text-end">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty tasks}">
                                        <tr>
                                            <td colspan="6" class="text-center py-4 text-muted">
                                                No tasks currently assigned to you!
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="t" items="${tasks}" begin="0" end="6">
                                            <tr>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="fw-semibold text-dark text-decoration-none">
                                                        <c:out value="${t.title}" />
                                                    </a>
                                                </td>
                                                <td class="small text-muted"><c:out value="${t.projectTitle}" /></td>
                                                <td><span class="${t.priority.badgeClass}">${t.priority.displayName}</span></td>
                                                <td><span class="${t.status.badgeClass}">${t.status.displayName}</span></td>
                                                <td class="small ${t.overdue ? 'text-danger fw-bold' : 'text-muted'}">
                                                    <fmt:formatDate value="${t.dueDate}" pattern="yyyy-MM-dd" />
                                                </td>
                                                <td class="text-end">
                                                    <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="btn btn-sm btn-outline-primary">
                                                        <i class="bi bi-eye-fill"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Assigned Projects Summary -->
            <div class="col-12 col-lg-4">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-folder-symlink text-primary me-2"></i> My Assigned Projects</span>
                        <span class="badge bg-secondary">${projects.size()}</span>
                    </div>
                    <div class="card-body p-0">
                        <ul class="list-group list-group-flush">
                            <c:choose>
                                <c:when test="${empty projects}">
                                    <li class="list-group-item text-center text-muted py-4">
                                        You have not been added to any active projects yet.
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="p" items="${projects}">
                                        <li class="list-group-item p-3">
                                            <div class="d-flex justify-content-between align-items-center mb-1">
                                                <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="fw-bold text-dark text-decoration-none small">
                                                    <c:out value="${p.title}" />
                                                </a>
                                                <span class="${p.status.badgeClass}" style="font-size: 0.65rem;">
                                                    ${p.status.displayName}
                                                </span>
                                            </div>
                                            <div class="d-flex justify-content-between small text-muted mb-1">
                                                <span>Manager: <c:out value="${p.managerName}" /></span>
                                                <span>${p.progressPercentage}%</span>
                                            </div>
                                            <div class="progress" style="height: 5px;">
                                                <div class="progress-bar bg-primary" role="progressbar" style="width: ${p.progressPercentage}%;"></div>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
