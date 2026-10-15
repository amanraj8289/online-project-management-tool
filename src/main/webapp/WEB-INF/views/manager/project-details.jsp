<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Project: ${project.title}" />
<c:set var="activeNav" value="projects" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <!-- Project Header Card -->
        <div class="card mb-4">
            <div class="card-body p-4">
                <div class="d-flex flex-wrap justify-content-between align-items-start gap-3">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-2">
                            <span class="${project.status.badgeClass}">${project.status.displayName}</span>
                            <c:if test="${project.overdue}">
                                <span class="badge bg-danger">Overdue</span>
                            </c:if>
                            <span class="text-muted small">
                                <i class="bi bi-calendar3 me-1"></i>
                                <fmt:formatDate value="${project.startDate}" pattern="yyyy-MM-dd" /> to 
                                <fmt:formatDate value="${project.dueDate}" pattern="yyyy-MM-dd" />
                            </span>
                        </div>
                        <h3 class="fw-bold text-dark mb-2"><c:out value="${project.title}" /></h3>
                        <p class="text-secondary mb-0"><c:out value="${project.description}" /></p>
                    </div>
                    <c:if test="${sessionScope.currentUser.admin || project.managerId == sessionScope.currentUser.id}">
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/projects/edit?id=${project.id}" class="btn btn-outline-secondary btn-sm">
                                <i class="bi bi-pencil-fill me-1"></i> Edit Project
                            </a>
                            <a href="${pageContext.request.contextPath}/tasks/create?projectId=${project.id}" class="btn btn-primary btn-sm">
                                <i class="bi bi-plus-lg me-1"></i> Add Task
                            </a>
                        </div>
                    </c:if>
                </div>

                <div class="row g-3 mt-3 pt-3 border-top">
                    <div class="col-6 col-md-3">
                        <small class="text-muted d-block">Project Manager</small>
                        <span class="fw-semibold text-dark"><c:out value="${project.managerName}" /></span>
                    </div>
                    <div class="col-6 col-md-3">
                        <small class="text-muted d-block">Team Members</small>
                        <span class="fw-semibold text-dark">${members.size()} Assigned</span>
                    </div>
                    <div class="col-6 col-md-3">
                        <small class="text-muted d-block">Task Completion</small>
                        <span class="fw-semibold text-dark">${project.completedTasks} / ${project.totalTasks} Tasks</span>
                    </div>
                    <div class="col-6 col-md-3">
                        <small class="text-muted d-block">Overall Progress</small>
                        <div class="d-flex align-items-center gap-2">
                            <div class="progress flex-grow-1" style="height: 8px;">
                                <div class="progress-bar bg-success" role="progressbar" style="width: ${project.progressPercentage}%;"></div>
                            </div>
                            <span class="fw-bold small">${project.progressPercentage}%</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- Left Column: Tasks in this Project -->
            <div class="col-12 col-lg-8">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-check2-square text-primary me-2"></i> Project Tasks</span>
                        <c:if test="${sessionScope.currentUser.admin || project.managerId == sessionScope.currentUser.id}">
                            <a href="${pageContext.request.contextPath}/tasks/create?projectId=${project.id}" class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-plus me-1"></i> New Task
                            </a>
                        </c:if>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Task</th>
                                    <th>Assignee</th>
                                    <th>Priority</th>
                                    <th>Status</th>
                                    <th>Due Date</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty tasks}">
                                        <tr>
                                            <td colspan="6" class="text-center py-4 text-muted">
                                                No tasks have been added to this project yet.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="t" items="${tasks}">
                                            <tr>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="fw-semibold text-dark text-decoration-none">
                                                        <c:out value="${t.title}" />
                                                    </a>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty t.assignedToName}">
                                                            <span class="small text-secondary"><c:out value="${t.assignedToName}" /></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary-subtle text-secondary small">Unassigned</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td><span class="${t.priority.badgeClass}">${t.priority.displayName}</span></td>
                                                <td><span class="${t.status.badgeClass}">${t.status.displayName}</span></td>
                                                <td class="small ${t.overdue ? 'text-danger fw-bold' : 'text-muted'}">
                                                    <fmt:formatDate value="${t.dueDate}" pattern="yyyy-MM-dd" />
                                                </td>
                                                <td class="text-end">
                                                    <div class="btn-group btn-group-sm">
                                                        <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="btn btn-outline-primary">
                                                            <i class="bi bi-eye-fill"></i>
                                                        </a>
                                                        <c:if test="${sessionScope.currentUser.admin || project.managerId == sessionScope.currentUser.id}">
                                                            <a href="${pageContext.request.contextPath}/tasks/edit?id=${t.id}" class="btn btn-outline-secondary">
                                                                <i class="bi bi-pencil-fill"></i>
                                                            </a>
                                                            <form action="${pageContext.request.contextPath}/tasks/delete" method="post" class="d-inline"
                                                                  onsubmit="return confirm('Delete task: ${t.title}?');">
                                                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                                                <input type="hidden" name="id" value="${t.id}" />
                                                                <button type="submit" class="btn btn-outline-danger">
                                                                    <i class="bi bi-trash-fill"></i>
                                                                </button>
                                                            </form>
                                                        </c:if>
                                                    </div>
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

            <!-- Right Column: Project Team Members -->
            <div class="col-12 col-lg-4">
                <div class="card mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-people-fill text-primary me-2"></i> Team Members</span>
                        <span class="badge bg-secondary">${members.size()}</span>
                    </div>
                    <div class="card-body p-0">
                        <ul class="list-group list-group-flush">
                            <!-- Project Manager Item -->
                            <li class="list-group-item d-flex justify-content-between align-items-center p-3 bg-light">
                                <div class="d-flex align-items-center gap-2">
                                    <div class="avatar-circle" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                        ${project.managerName.substring(0, 1)}
                                    </div>
                                    <div>
                                        <div class="fw-semibold small"><c:out value="${project.managerName}" /></div>
                                        <span class="badge bg-primary" style="font-size: 0.65rem;">Manager</span>
                                    </div>
                                </div>
                            </li>

                            <!-- Assigned Members -->
                            <c:forEach var="m" items="${members}">
                                <li class="list-group-item d-flex justify-content-between align-items-center p-3">
                                    <div class="d-flex align-items-center gap-2">
                                        <div class="avatar-circle" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                            ${m.userName.substring(0, 1)}
                                        </div>
                                        <div>
                                            <div class="fw-semibold small"><c:out value="${m.userName}" /></div>
                                            <small class="text-muted"><c:out value="${m.userEmail}" /></small>
                                        </div>
                                    </div>
                                    <c:if test="${sessionScope.currentUser.admin || project.managerId == sessionScope.currentUser.id}">
                                        <form action="${pageContext.request.contextPath}/projects/member/remove" method="post" class="d-inline"
                                              onsubmit="return confirm('Remove ${m.userName} from this project?');">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                            <input type="hidden" name="projectId" value="${project.id}" />
                                            <input type="hidden" name="userId" value="${m.userId}" />
                                            <button type="submit" class="btn btn-sm btn-outline-danger p-1 lh-1" title="Remove member">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>

                    <!-- Add Team Member form for authorized PM or Admin -->
                    <c:if test="${sessionScope.currentUser.admin || project.managerId == sessionScope.currentUser.id}">
                        <c:if test="${not empty availableUsers}">
                            <div class="card-footer bg-white p-3 border-top">
                                <form action="${pageContext.request.contextPath}/projects/member/add" method="post" class="d-flex gap-2">
                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                    <input type="hidden" name="projectId" value="${project.id}" />
                                    <select name="userId" class="form-select form-select-sm" required>
                                        <option value="">Select team member...</option>
                                        <c:forEach var="u" items="${availableUsers}">
                                            <option value="${u.id}">${u.name} (${u.role.displayName})</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-primary flex-shrink-0">
                                        <i class="bi bi-plus-lg"></i> Add
                                    </button>
                                </form>
                            </div>
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
