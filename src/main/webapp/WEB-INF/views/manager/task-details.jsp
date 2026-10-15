<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Task Details: ${task.title}" />
<c:set var="activeNav" value="tasks" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="row g-4">
            <!-- Left Column: Task Overview & Comments Thread -->
            <div class="col-12 col-lg-8">
                <div class="card mb-4">
                    <div class="card-body p-4">
                        <div class="d-flex justify-content-between align-items-start gap-2 mb-3">
                            <div>
                                <span class="${task.status.badgeClass} me-1">${task.status.displayName}</span>
                                <span class="${task.priority.badgeClass}">${task.priority.displayName}</span>
                                <c:if test="${task.overdue}">
                                    <span class="badge bg-danger ms-1">Overdue</span>
                                </c:if>
                            </div>
                            <c:if test="${sessionScope.currentUser.admin || project.managerId == sessionScope.currentUser.id}">
                                <div class="btn-group btn-group-sm">
                                    <a href="${pageContext.request.contextPath}/tasks/edit?id=${task.id}" class="btn btn-outline-secondary">
                                        <i class="bi bi-pencil-fill me-1"></i> Edit
                                    </a>
                                </div>
                            </c:if>
                        </div>

                        <h3 class="fw-bold text-dark mb-2"><c:out value="${task.title}" /></h3>
                        <p class="text-secondary fs-6 mb-4"><c:out value="${task.description}" /></p>

                        <!-- Quick Status Transition Form -->
                        <div class="p-3 bg-light rounded-3 border">
                            <form action="${pageContext.request.contextPath}/tasks/status" method="post" class="row g-2 align-items-center">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                <input type="hidden" name="taskId" value="${task.id}" />

                                <div class="col-auto">
                                    <label class="form-label mb-0 fw-semibold small text-secondary">Update Status:</label>
                                </div>
                                <div class="col-auto">
                                    <select name="status" class="form-select form-select-sm">
                                        <c:forEach var="st" items="${statuses}">
                                            <option value="${st.name()}" ${task.status == st ? 'selected' : ''}>
                                                ${st.displayName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col">
                                    <input type="text" name="note" class="form-control form-control-sm" placeholder="Optional progress note...">
                                </div>
                                <div class="col-auto">
                                    <button type="submit" class="btn btn-sm btn-primary">Update</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Collaboration Comments Section -->
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-chat-left-text-fill text-primary me-2"></i> Progress Notes & Collaboration (${comments.size()})</span>
                    </div>
                    <div class="card-body p-4">
                        <!-- Add Comment Box -->
                        <form action="${pageContext.request.contextPath}/tasks/comment" method="post" class="mb-4">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                            <input type="hidden" name="taskId" value="${task.id}" />
                            <div class="mb-2">
                                <textarea name="comment" class="form-control" rows="2" placeholder="Add a progress update, note, or blocker..." required></textarea>
                            </div>
                            <div class="text-end">
                                <button type="submit" class="btn btn-sm btn-primary">
                                    <i class="bi bi-send-fill me-1"></i> Post Comment
                                </button>
                            </div>
                        </form>

                        <!-- Comments List -->
                        <c:choose>
                            <c:when test="${empty comments}">
                                <div class="text-center py-4 text-muted small">
                                    No comments recorded yet. Add the first progress update above.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex flex-column gap-3">
                                    <c:forEach var="c" items="${comments}">
                                        <div class="d-flex gap-3 p-3 bg-light rounded-3 border">
                                            <div class="avatar-circle" style="width: 34px; height: 34px; font-size: 0.85rem;">
                                                ${c.userName.substring(0, 1)}
                                            </div>
                                            <div class="flex-grow-1">
                                                <div class="d-flex justify-content-between align-items-center mb-1">
                                                    <div>
                                                        <span class="fw-bold small text-dark"><c:out value="${c.userName}" /></span>
                                                        <span class="${c.userRole.badgeClass} ms-1" style="font-size: 0.65rem;">
                                                            ${c.userRole.displayName}
                                                        </span>
                                                    </div>
                                                    <small class="text-muted" style="font-size: 0.75rem;">
                                                        <fmt:formatDate value="${c.createdAt}" pattern="MMM dd, yyyy HH:mm" />
                                                    </small>
                                                </div>
                                                <div class="text-secondary small">
                                                    <c:out value="${c.comment}" />
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Right Column: Task Meta & Details -->
            <div class="col-12 col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-info-circle-fill text-primary me-2"></i> Task Metadata</span>
                    </div>
                    <div class="card-body p-3">
                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Associated Project:</span>
                                <a href="${pageContext.request.contextPath}/projects/view?id=${task.projectId}" class="fw-semibold text-decoration-none">
                                    <c:out value="${task.projectTitle}" />
                                </a>
                            </li>
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Assigned To:</span>
                                <span class="fw-semibold">
                                    <c:out value="${not empty task.assignedToName ? task.assignedToName : 'Unassigned'}" />
                                </span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Start Date:</span>
                                <span><fmt:formatDate value="${task.startDate}" pattern="yyyy-MM-dd" /></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Due Date:</span>
                                <span class="${task.overdue ? 'text-danger fw-bold' : ''}">
                                    <fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />
                                </span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Created Date:</span>
                                <span><fmt:formatDate value="${task.createdAt}" pattern="yyyy-MM-dd HH:mm" /></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Last Updated:</span>
                                <span><fmt:formatDate value="${task.updatedAt}" pattern="yyyy-MM-dd HH:mm" /></span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
