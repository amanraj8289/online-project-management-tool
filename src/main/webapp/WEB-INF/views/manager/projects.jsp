<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Managed Projects" />
<c:set var="activeNav" value="projects" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-folder2-open text-primary fs-5"></i>
                    <span class="fs-5 fw-bold">Projects You Manage</span>
                    <span class="badge bg-secondary ms-2">${projects.size()} projects</span>
                </div>
                <a href="${pageContext.request.contextPath}/projects/create" class="btn btn-primary btn-sm">
                    <i class="bi bi-plus-circle-fill me-1"></i> Create Project
                </a>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Status</th>
                            <th>Start Date</th>
                            <th>Due Date</th>
                            <th>Team Members</th>
                            <th>Progress</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${projects}">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="fw-bold text-dark text-decoration-none">
                                        <c:out value="${p.title}" />
                                    </a>
                                    <div class="text-muted small text-truncate" style="max-width: 280px;">
                                        <c:out value="${p.description}" />
                                    </div>
                                </td>
                                <td>
                                    <span class="${p.status.badgeClass}">${p.status.displayName}</span>
                                    <c:if test="${p.overdue}">
                                        <span class="badge bg-danger ms-1">Overdue</span>
                                    </c:if>
                                </td>
                                <td class="small text-muted"><fmt:formatDate value="${p.startDate}" pattern="yyyy-MM-dd" /></td>
                                <td class="small text-muted"><fmt:formatDate value="${p.dueDate}" pattern="yyyy-MM-dd" /></td>
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        <i class="bi bi-people-fill text-secondary me-1"></i> ${p.teamMemberCount}
                                    </span>
                                </td>
                                <td style="min-width: 140px;">
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
                                        <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="btn btn-outline-primary" title="View Project">
                                            <i class="bi bi-eye-fill"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/projects/edit?id=${p.id}" class="btn btn-outline-secondary" title="Edit Project">
                                            <i class="bi bi-pencil-fill"></i>
                                        </a>
                                        <form action="${pageContext.request.contextPath}/projects/delete" method="post" class="d-inline"
                                              onsubmit="return confirm('Delete project: ${p.title}? This cannot be undone.');">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                            <input type="hidden" name="id" value="${p.id}" />
                                            <button type="submit" class="btn btn-outline-danger" title="Delete Project">
                                                <i class="bi bi-trash-fill"></i>
                                            </button>
                                        </form>
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

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
