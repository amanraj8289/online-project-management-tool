<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Administrator Dashboard" />
<c:set var="activeNav" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <!-- Top KPI Metric Cards -->
        <div class="row g-3 mb-4">
            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card primary">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">Total Users</div>
                            <div class="metric-value">${metrics.totalUsers}</div>
                        </div>
                        <div class="metric-icon-box bg-primary-subtle text-primary">
                            <i class="bi bi-people-fill"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">Active in system</div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card info">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="metric-label">Active Projects</div>
                            <div class="metric-value">${metrics.activeProjects}</div>
                        </div>
                        <div class="metric-icon-box bg-info-subtle text-info">
                            <i class="bi bi-folder-fill"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">Out of ${metrics.totalProjects} total projects</div>
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
                            <i class="bi bi-exclamation-triangle-fill"></i>
                        </div>
                    </div>
                    <div class="mt-2 small text-danger fw-semibold">Action required</div>
                </div>
            </div>
        </div>

        <!-- Visual Analytics Row (Chart.js) -->
        <div class="row g-4 mb-4">
            <div class="col-12 col-lg-5">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-pie-chart-fill text-primary me-2"></i> Task Status Distribution</span>
                        <span class="badge bg-light text-dark border">Real-time</span>
                    </div>
                    <div class="card-body" style="height: 320px; position: relative;">
                        <canvas id="taskStatusChart"></canvas>
                    </div>
                </div>
            </div>

            <div class="col-12 col-lg-7">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-bar-chart-fill text-primary me-2"></i> Project Tasks Breakdown</span>
                        <a href="${pageContext.request.contextPath}/projects" class="btn btn-sm btn-outline-primary">View Projects</a>
                    </div>
                    <div class="card-body" style="height: 320px; position: relative;">
                        <canvas id="projectProgressBarChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent System Activity Audit Trail -->
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-clock-history text-primary me-2"></i> Recent Audit Activity</span>
                <span class="badge bg-secondary">Latest 10 actions</span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>User</th>
                            <th>Action Type</th>
                            <th>Details</th>
                            <th>Timestamp</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="log" items="${recentLogs}">
                            <tr>
                                <td class="fw-semibold">
                                    <c:out value="${not empty log.userName ? log.userName : 'System'}" />
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border"><c:out value="${log.actionType}" /></span>
                                </td>
                                <td class="text-secondary">
                                    <c:out value="${log.description}" />
                                </td>
                                <td class="text-muted small">
                                    <fmt:formatDate value="${log.createdAt}" pattern="MMM dd, yyyy HH:mm:ss" />
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Render Chart.js data directly from backend metrics -->
<script>
    document.addEventListener('DOMContentLoaded', () => {
        const taskData = {
            'To Do': ${metrics.taskStatusDistribution['TODO'] != null ? metrics.taskStatusDistribution['TODO'] : 0},
            'In Progress': ${metrics.taskStatusDistribution['IN_PROGRESS'] != null ? metrics.taskStatusDistribution['IN_PROGRESS'] : 0},
            'In Review': ${metrics.taskStatusDistribution['IN_REVIEW'] != null ? metrics.taskStatusDistribution['IN_REVIEW'] : 0},
            'Completed': ${metrics.taskStatusDistribution['COMPLETED'] != null ? metrics.taskStatusDistribution['COMPLETED'] : 0}
        };
        initTaskStatusChart('taskStatusChart', taskData);

        const projectList = [
            <c:forEach var="p" items="${progressList}" varStatus="status">
            {
                projectTitle: '${p.projectTitle}',
                completedTasks: ${p.completedTasks},
                inProgressTasks: ${p.inProgressTasks},
                todoTasks: ${p.todoTasks}
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        initProjectProgressBarChart('projectProgressBarChart', projectList);
    });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
