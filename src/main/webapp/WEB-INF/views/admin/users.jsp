<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="User Accounts" />
<c:set var="activeNav" value="users" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="card mb-4">
            <div class="card-header d-flex flex-wrap justify-content-between align-items-center gap-2">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-people-fill text-primary fs-5"></i>
                    <span class="fs-5 fw-bold">User Management</span>
                    <span class="badge bg-secondary ms-2">${totalCount} users</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary btn-sm">
                        <i class="bi bi-person-plus-fill me-1"></i> Add New User
                    </a>
                </div>
            </div>
            <div class="card-body border-bottom bg-light py-3">
                <form action="${pageContext.request.contextPath}/users" method="get" class="row g-2 align-items-center">
                    <div class="col-12 col-md-4">
                        <div class="input-group input-group-sm">
                            <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                            <input type="text" name="search" class="form-control" placeholder="Search by name or email..."
                                   value="<c:out value='${searchKeyword}' />">
                        </div>
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-sm btn-secondary">Filter</button>
                        <c:if test="${not empty searchKeyword}">
                            <a href="${pageContext.request.contextPath}/users" class="btn btn-sm btn-outline-secondary">Reset</a>
                        </c:if>
                    </div>
                </form>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>User</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>
                                    <div class="d-flex align-items-center gap-2">
                                        <div class="avatar-circle" style="width: 32px; height: 32px; font-size: 0.8rem;">
                                            ${u.name.substring(0, 1)}
                                        </div>
                                        <span class="fw-semibold text-dark"><c:out value="${u.name}" /></span>
                                        <c:if test="${u.id == sessionScope.currentUser.id}">
                                            <span class="badge bg-warning text-dark" style="font-size: 0.65rem;">You</span>
                                        </c:if>
                                    </div>
                                </td>
                                <td class="text-secondary"><c:out value="${u.email}" /></td>
                                <td>
                                    <span class="${u.role.badgeClass}">${u.role.displayName}</span>
                                </td>
                                <td>
                                    <span class="${u.accountStatus.badgeClass}">${u.accountStatus.displayName}</span>
                                </td>
                                <td class="text-muted small">
                                    <fmt:formatDate value="${u.createdAt}" pattern="yyyy-MM-dd" />
                                </td>
                                <td class="text-end">
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/users/edit?id=${u.id}" class="btn btn-outline-secondary" title="Edit User">
                                            <i class="bi bi-pencil-fill"></i>
                                        </a>
                                        <c:if test="${u.id != sessionScope.currentUser.id}">
                                            <form action="${pageContext.request.contextPath}/users/delete" method="post" class="d-inline"
                                                  onsubmit="return confirm('Are you sure you want to delete user: ${u.name}? All associated assignments will be removed.');">
                                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                                                <input type="hidden" name="id" value="${u.id}" />
                                                <button type="submit" class="btn btn-outline-danger" title="Delete User">
                                                    <i class="bi bi-trash-fill"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="card-footer bg-white d-flex justify-content-between align-items-center py-2">
                    <small class="text-muted">Page ${currentPage} of ${totalPages}</small>
                    <nav aria-label="Users pagination">
                        <ul class="pagination pagination-sm mb-0">
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/users?page=${currentPage - 1}&search=${searchKeyword}">Previous</a>
                            </li>
                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <li class="page-item ${currentPage == p ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/users?page=${p}&search=${searchKeyword}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/users?page=${currentPage + 1}&search=${searchKeyword}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
