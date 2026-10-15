<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Assigned Tasks" />
<c:set var="activeNav" value="my-tasks" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="card mb-4">
            <div class="card-header bg-white py-3">
                <div class="d-flex flex-wrap justify-content-between align-items-center gap-2">
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-check2-circle text-primary fs-5"></i>
                        <span class="fs-5 fw-bold">My Task Deliverables</span>
                        <span class="badge bg-secondary ms-2">${tasks.size()} matching</span>
                    </div>
                </div>
            </div>

            <!-- Filter Bar -->
            <div class="card-body border-bottom bg-light py-3">
                <form action="${pageContext.request.contextPath}/member/my-tasks" method="get" class="row g-2 align-items-center">
                    <div class="col-12 col-md-4">
                        <div class="input-group input-group-sm">
                            <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                            <input type="text" name="search" class="form-control" placeholder="Search tasks by title..."
                                   value="<c:out value='${searchKeyword}' />">
                        </div>
                    </div>

                    <div class="col-6 col-md-3">
                        <select name="status" class="form-select form-select-sm" onchange="this.form.submit()">
                            <option value="">All Statuses</option>
                            <c:forEach var="st" items="${statuses}">
                                <option value="${st.name()}" ${selectedStatus == st.name() ? 'selected' : ''}>
                                    ${st.displayName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-6 col-md-3">
                        <select name="priority" class="form-select form-select-sm" onchange="this.form.submit()">
                            <option value="">All Priorities</option>
                            <c:forEach var="pr" items="${priorities}">
                                <option value="${pr.name()}" ${selectedPriority == pr.name() ? 'selected' : ''}>
                                    ${pr.displayName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-12 col-md-2 d-flex gap-1">
                        <button type="submit" class="btn btn-sm btn-secondary flex-grow-1">Filter</button>
                        <c:if test="${not empty selectedStatus || not empty selectedPriority || not empty searchKeyword}">
                            <a href="${pageContext.request.contextPath}/member/my-tasks" class="btn btn-sm btn-outline-secondary">Reset</a>
                        </c:if>
                    </div>
                </form>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Task Title</th>
                            <th>Project</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Due Date</th>
                            <th class="text-end">Update Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty tasks}">
                                <tr>
                                    <td colspan="6" class="text-center py-5 text-muted">
                                        <i class="bi bi-inbox fs-2 d-block mb-2 text-secondary"></i>
                                        No matching tasks found.
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
                                            <div class="text-muted small text-truncate" style="max-width: 280px;">
                                                <c:out value="${t.description}" />
                                            </div>
                                        </td>
                                        <td class="small text-secondary"><c:out value="${t.projectTitle}" /></td>
                                        <td><span class="${t.priority.badgeClass}">${t.priority.displayName}</span></td>
                                        <td><span class="${t.status.badgeClass}">${t.status.displayName}</span></td>
                                        <td class="small ${t.overdue ? 'text-danger fw-bold' : 'text-muted'}">
                                            <fmt:formatDate value="${t.dueDate}" pattern="yyyy-MM-dd" />
                                            <c:if test="${t.overdue}">
                                                <span class="badge bg-danger ms-1">Overdue</span>
                                            </c:if>
                                        </td>
                                        <td class="text-end">
                                            <form action="${pageContext.request.contextPath}/tasks/status" method="post" class="d-inline-flex gap-1 justify-content-end">
                                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                                <input type="hidden" name="taskId" value="${t.id}" />
                                                <select name="status" class="form-select form-select-sm" style="width: 130px;" onchange="this.form.submit()">
                                                    <c:forEach var="st" items="${statuses}">
                                                        <option value="${st.name()}" ${t.status == st ? 'selected' : ''}>
                                                            ${st.displayName}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <a href="${pageContext.request.contextPath}/tasks/view?id=${t.id}" class="btn btn-sm btn-outline-primary" title="Add Note / Details">
                                                    <i class="bi bi-chat-left-text"></i>
                                                </a>
                                            </form>
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
