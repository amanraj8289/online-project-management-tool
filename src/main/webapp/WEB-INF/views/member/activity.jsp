<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Activity Feed" />
<c:set var="activeNav" value="activity" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="card shadow-sm">
            <div class="card-header bg-white py-3">
                <span class="fw-bold"><i class="bi bi-clock-history text-primary me-2"></i> Your Recent Activity Log</span>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty logs}">
                        <div class="p-5 text-center text-muted">
                            <i class="bi bi-calendar2-x fs-1 d-block mb-3 text-secondary"></i>
                            No activity records logged yet.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="list-group list-group-flush">
                            <c:forEach var="log" items="${logs}">
                                <div class="list-group-item p-3 d-flex align-items-start gap-3">
                                    <div class="mt-1">
                                        <div class="avatar-circle" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                            <i class="bi bi-activity"></i>
                                        </div>
                                    </div>
                                    <div class="flex-grow-1">
                                        <div class="d-flex justify-content-between align-items-center mb-1">
                                            <span class="badge bg-light text-dark border"><c:out value="${log.actionType}" /></span>
                                            <small class="text-muted"><fmt:formatDate value="${log.createdAt}" pattern="MMM dd, yyyy HH:mm:ss" /></small>
                                        </div>
                                        <p class="mb-0 text-dark small"><c:out value="${log.description}" /></p>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
