<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Task Management" />
<c:set var="activeNav" value="tasks" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-list-task text-primary fs-5"></i>
                    <span class="fs-5 fw-bold">All Assigned Tasks</span>
                    <span class="badge bg-secondary ms-2">${tasks.size()} total</span>
                </div>
                <a href="${pageContext.request.contextPath}/tasks/create" class="btn btn-primary btn-sm">
                    <i class="bi bi-plus-lg me-1"></i> Create Task
                </a>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Task Title</th>
                            <th>Project</th>
                            <th>Assigned Member</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Timeline</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty tasks}">
                                <tr>
                                    <td colspan="7" class="text-center py-4 text-muted">
                                        No tasks found. Click "Create Task" to assign one!
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="t" items="${tasks}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="fw-bold text-dark text-decoration-none">
                                                <c:out value="${t.title}" />
                                            </a>
                                            <div class="text-muted small text-truncate" style="max-width: 250px;">
                                                <c:out value="${t.description}" />
                                            </div>
                                        </td>
                                        <td class="text-secondary small">
                                            <c:out value="${t.projectTitle}" />
                                        </td>
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
                                                <span class="badge bg-danger ms-1">Overdue</span>
                                            </c:if>
                                        </td>
                                        <td class="text-end">
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="btn btn-outline-primary" title="View Task Details">
                                                    <i class="bi bi-eye-fill"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/tasks/edit?id=${t.id}" class="btn btn-outline-secondary" title="Edit Task">
                                                    <i class="bi bi-pencil-fill"></i>
                                                </a>
                                                <form action="${pageContext.request.contextPath}/tasks/delete" method="post" class="d-inline"
                                                      onsubmit="return confirm('Delete task: ${t.title}?');">
                                                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                                    <input type="hidden" name="id" value="${t.id}" />
                                                    <button type="submit" class="btn btn-outline-danger" title="Delete Task">
                                                        <i class="bi bi-trash-fill"></i>
                                                    </button>
                                                </form>
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
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
