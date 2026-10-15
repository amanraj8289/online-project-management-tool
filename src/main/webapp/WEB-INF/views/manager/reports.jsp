<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Project Progress Reports" />
<c:set var="activeNav" value="reports" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <!-- Analytics Overview Cards -->
        <div class="row g-3 mb-4">
            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card primary">
                    <div class="metric-label">Total Assigned Tasks</div>
                    <div class="metric-value">${metrics.totalTasks}</div>
                    <div class="small text-muted mt-2">Across all managed projects</div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card success">
                    <div class="metric-label">Completed Velocity</div>
                    <div class="metric-value">${metrics.completedTasks}</div>
                    <div class="small text-success fw-semibold mt-2">${metrics.overallCompletionRate}% completion rate</div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card info">
                    <div class="metric-label">In Progress</div>
                    <div class="metric-value">${metrics.inProgressTasks}</div>
                    <div class="small text-muted mt-2">Active development</div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-xl-3">
                <div class="metric-card danger">
                    <div class="metric-label">Overdue Tasks</div>
                    <div class="metric-value">${metrics.overdueTasks}</div>
                    <div class="small text-danger fw-semibold mt-2">Past scheduled deadline</div>
                </div>
            </div>
        </div>

        <!-- Chart.js Visuals -->
        <div class="row g-4 mb-4">
            <div class="col-12 col-lg-5">
                <div class="card h-100">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-pie-chart-fill text-primary me-2"></i> Task Status Distribution</span>
                    </div>
                    <div class="card-body" style="height: 300px; position: relative;">
                        <canvas id="taskStatusChart"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-12 col-lg-7">
                <div class="card h-100">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-bar-chart-fill text-primary me-2"></i> Task Completion by Project</span>
                    </div>
                    <div class="card-body" style="height: 300px; position: relative;">
                        <canvas id="projectProgressBarChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Project Progress Matrix Table -->
        <div class="card">
            <div class="card-header bg-white py-3">
                <span class="fw-bold"><i class="bi bi-table text-primary me-2"></i> Detailed Project Progress Breakdown</span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Project</th>
                            <th>Status</th>
                            <th>Completed Tasks</th>
                            <th>In Progress</th>
                            <th>To Do</th>
                            <th>Total Tasks</th>
                            <th>Completion Rate</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${progressList}">
                            <tr>
                                <td class="fw-bold"><c:out value="${p.projectTitle}" /></td>
                                <td>
                                    <span class="badge bg-light text-dark border"><c:out value="${p.status}" /></span>
                                    <c:if test="${p.overdue}">
                                        <span class="badge bg-danger ms-1">Overdue</span>
                                    </c:if>
                                </td>
                                <td class="text-success fw-semibold">${p.completedTasks}</td>
                                <td class="text-primary fw-semibold">${p.inProgressTasks}</td>
                                <td class="text-secondary">${p.todoTasks}</td>
                                <td class="fw-bold">${p.totalTasks}</td>
                                <td style="min-width: 140px;">
                                    <div class="d-flex justify-content-between small text-muted mb-1">
                                        <span>Progress</span>
                                        <span class="fw-bold">${p.progressPercentage}%</span>
                                    </div>
                                    <div class="progress" style="height: 6px;">
                                        <div class="progress-bar bg-success" role="progressbar" style="width: ${p.progressPercentage}%;"></div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Chart.js initialization script -->
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
