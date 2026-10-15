<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Notifications" />
<c:set var="activeNav" value="notifications" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-bell-fill text-primary me-2"></i> System & Task Notifications</span>
                <form action="${pageContext.request.contextPath}/notifications/read-all" method="post" class="d-inline">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                    <button type="submit" class="btn btn-sm btn-outline-secondary">
                        <i class="bi bi-check2-all me-1"></i> Mark All as Read
                    </button>
                </form>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty notifications}">
                        <div class="p-5 text-center text-muted">
                            <i class="bi bi-bell-slash fs-1 d-block mb-3 text-secondary"></i>
                            <p class="mb-0">No notifications found. You're completely up to date!</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="list-group list-group-flush">
                            <c:forEach var="n" items="${notifications}">
                                <div class="list-group-item p-3 ${!n.read ? 'bg-light border-start border-4 border-primary' : ''} d-flex justify-content-between align-items-center">
                                    <div class="d-flex align-items-start gap-3">
                                        <div class="mt-1">
                                            <c:choose>
                                                <c:when test="${n.notificationType == 'TASK_ASSIGNED'}">
                                                    <i class="bi bi-clipboard-check-fill text-primary fs-5"></i>
                                                </c:when>
                                                <c:when test="${n.notificationType == 'PROJECT_UPDATE'}">
                                                    <i class="bi bi-folder-fill text-info fs-5"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="bi bi-info-circle-fill text-secondary fs-5"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <p class="mb-1 text-dark ${!n.read ? 'fw-bold' : ''}">
                                                <c:out value="${n.message}" />
                                            </p>
                                            <small class="text-muted"><fmt:formatDate value="${n.createdAt}" pattern="MMM dd, yyyy HH:mm" /></small>
                                        </div>
                                    </div>
                                    <c:if test="${!n.read}">
                                        <form action="${pageContext.request.contextPath}/notifications/read" method="post">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                            <input type="hidden" name="id" value="${n.id}" />
                                            <button type="submit" class="btn btn-sm btn-light border" title="Mark as read">
                                                <i class="bi bi-check-lg"></i>
                                            </button>
                                        </form>
                                    </c:if>
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
