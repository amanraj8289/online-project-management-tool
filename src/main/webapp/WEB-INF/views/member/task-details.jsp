<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Task: ${task.title}" />
<c:set var="activeNav" value="my-tasks" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="row g-4">
            <div class="col-12 col-lg-8">
                <!-- Task Details Card -->
                <div class="card mb-4 shadow-sm">
                    <div class="card-body p-4">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <div>
                                <span class="${task.status.badgeClass} me-1">${task.status.displayName}</span>
                                <span class="${task.priority.badgeClass}">${task.priority.displayName}</span>
                                <c:if test="${task.overdue}">
                                    <span class="badge bg-danger ms-1">Overdue</span>
                                </c:if>
                            </div>
                            <span class="text-muted small">
                                Due: <fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />
                            </span>
                        </div>

                        <h3 class="fw-bold text-dark mb-2"><c:out value="${task.title}" /></h3>
                        <p class="text-secondary fs-6 mb-4"><c:out value="${task.description}" /></p>

                        <!-- Member Status Update Action -->
                        <div class="p-3 bg-light rounded-3 border">
                            <form action="${pageContext.request.contextPath}/tasks/status" method="post" class="row g-2 align-items-center">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                <input type="hidden" name="taskId" value="${task.id}" />

                                <div class="col-auto">
                                    <label class="form-label mb-0 fw-semibold small text-secondary">Change Status:</label>
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
                                    <input type="text" name="note" class="form-control form-control-sm" placeholder="Add status transition note...">
                                </div>
                                <div class="col-auto">
                                    <button type="submit" class="btn btn-sm btn-primary">Save Status</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Discussion & Comments -->
                <div class="card shadow-sm">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-chat-left-dots-fill text-primary me-2"></i> Progress Updates & Discussion (${comments.size()})</span>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/tasks/comment" method="post" class="mb-4">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                            <input type="hidden" name="taskId" value="${task.id}" />
                            <div class="mb-2">
                                <textarea name="comment" class="form-control" rows="2" placeholder="Post a comment or progress update..." required></textarea>
                            </div>
                            <div class="text-end">
                                <button type="submit" class="btn btn-sm btn-primary">
                                    <i class="bi bi-send-fill me-1"></i> Post Note
                                </button>
                            </div>
                        </form>

                        <c:choose>
                            <c:when test="${empty comments}">
                                <div class="text-center py-4 text-muted small">
                                    No notes yet. Add your progress report above.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex flex-column gap-3">
                                    <c:forEach var="c" items="${comments}">
                                        <div class="d-flex gap-3 p-3 bg-light rounded-3 border">
                                            <div class="avatar-circle" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                                ${c.userName.substring(0, 1)}
                                            </div>
                                            <div class="flex-grow-1">
                                                <div class="d-flex justify-content-between align-items-center mb-1">
                                                    <div>
                                                        <span class="fw-bold small text-dark"><c:out value="${c.userName}" /></span>
                                                        <span class="${c.userRole.badgeClass} ms-1" style="font-size: 0.65rem;">${c.userRole.displayName}</span>
                                                    </div>
                                                    <small class="text-muted" style="font-size: 0.75rem;">
                                                        <fmt:formatDate value="${c.createdAt}" pattern="MMM dd, yyyy HH:mm" />
                                                    </small>
                                                </div>
                                                <div class="text-secondary small"><c:out value="${c.comment}" /></div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Right Column: Project Summary -->
            <div class="col-12 col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-folder-fill text-primary me-2"></i> Project Context</span>
                    </div>
                    <div class="card-body p-3">
                        <h6 class="fw-bold mb-1"><c:out value="${project.title}" /></h6>
                        <p class="text-secondary small mb-3"><c:out value="${project.description}" /></p>

                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Project Manager:</span>
                                <span class="fw-semibold"><c:out value="${project.managerName}" /></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Project Due Date:</span>
                                <span><fmt:formatDate value="${project.dueDate}" pattern="yyyy-MM-dd" /></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between px-0 py-2">
                                <span class="text-muted">Total Tasks:</span>
                                <span>${project.totalTasks}</span>
                            </li>
                        </ul>

                        <div class="mt-3 pt-3 border-top text-center">
                            <a href="${pageContext.request.contextPath}/projects/view?id=${project.id}" class="btn btn-sm btn-outline-primary w-100">
                                View Full Project
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
