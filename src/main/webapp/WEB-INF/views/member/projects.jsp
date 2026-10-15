<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Projects" />
<c:set var="activeNav" value="projects" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="card shadow-sm">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-folder-check text-primary fs-5"></i>
                    <span class="fs-5 fw-bold">Projects You Belong To</span>
                    <span class="badge bg-secondary ms-2">${projects.size()} projects</span>
                </div>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Project Title</th>
                            <th>Manager</th>
                            <th>Status</th>
                            <th>Timeline</th>
                            <th>Progress</th>
                            <th class="text-end">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty projects}">
                                <tr>
                                    <td colspan="6" class="text-center py-5 text-muted">
                                        <i class="bi bi-folder-x fs-2 d-block mb-2 text-secondary"></i>
                                        You are not assigned to any projects at this time.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${projects}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="fw-bold text-dark text-decoration-none">
                                                <c:out value="${p.title}" />
                                            </a>
                                            <div class="text-muted small text-truncate" style="max-width: 320px;">
                                                <c:out value="${p.description}" />
                                            </div>
                                        </td>
                                        <td>
                                            <span class="small text-secondary"><c:out value="${p.managerName}" /></span>
                                        </td>
                                        <td>
                                            <span class="${p.status.badgeClass}">${p.status.displayName}</span>
                                        </td>
                                        <td class="small text-muted">
                                            <fmt:formatDate value="${p.startDate}" pattern="yyyy-MM-dd" /> to 
                                            <fmt:formatDate value="${p.dueDate}" pattern="yyyy-MM-dd" />
                                        </td>
                                        <td style="min-width: 140px;">
                                            <div class="d-flex justify-content-between small text-muted mb-1">
                                                <span>${p.completedTasks}/${p.totalTasks}</span>
                                                <span class="fw-bold">${p.progressPercentage}%</span>
                                            </div>
                                            <div class="progress" style="height: 6px;">
                                                <div class="progress-bar bg-success" role="progressbar" style="width: ${p.progressPercentage}%;"></div>
                                            </div>
                                        </td>
                                        <td class="text-end">
                                            <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="btn btn-sm btn-outline-primary">
                                                <i class="bi bi-eye-fill me-1"></i> View
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
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
