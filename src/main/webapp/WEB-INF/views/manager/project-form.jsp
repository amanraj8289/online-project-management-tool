<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="isEdit" value="${not empty project}" />
<c:set var="pageTitle" value="${isEdit ? 'Edit Project' : 'Create New Project'}" />
<c:set var="activeNav" value="projects" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

<div id="main-content">
    <jsp:include page="/WEB-INF/views/common/topbar.jsp" />

    <main class="p-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="row justify-content-center">
            <div class="col-12 col-md-10 col-lg-8">
                <div class="card shadow-sm">
                    <div class="card-header bg-white py-3">
                        <div class="d-flex align-items-center gap-2">
                            <i class="bi ${isEdit ? 'bi-pencil-square' : 'bi-folder-plus'} text-primary fs-5"></i>
                            <h5 class="card-title mb-0 fw-bold">${isEdit ? 'Edit Project Specifications' : 'Initialize New Project'}</h5>
                        </div>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/projects/${isEdit ? 'edit' : 'create'}" method="post" class="needs-validation" novalidate>
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                            <c:if test="${isEdit}">
                                <input type="hidden" name="id" value="${project.id}" />
                            </c:if>

                            <div class="mb-3">
                                <label for="title" class="form-label fw-semibold">Project Title <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title"
                                       value="<c:out value='${isEdit ? project.title : title}' />" required maxlength="200">
                                <div class="invalid-feedback">Please enter a valid project title (max 200 characters).</div>
                            </div>

                            <div class="mb-3">
                                <label for="description" class="form-label fw-semibold">Project Description</label>
                                <textarea class="form-control" id="description" name="description" rows="4"><c:out value="${isEdit ? project.description : description}" /></textarea>
                            </div>

                            <div class="row g-3 mb-3">
                                <div class="col-12 col-sm-6">
                                    <label for="startDate" class="form-label fw-semibold">Start Date <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="startDate" name="startDate"
                                           value="${isEdit ? project.startDate : startDate}" required>
                                    <div class="invalid-feedback">Start date is required.</div>
                                </div>
                                <div class="col-12 col-sm-6">
                                    <label for="dueDate" class="form-label fw-semibold">Due Date / Deadline <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="dueDate" name="dueDate"
                                           value="${isEdit ? project.dueDate : dueDate}" required>
                                    <div class="invalid-feedback">Due date is required and must be on or after start date.</div>
                                </div>
                            </div>

                            <div class="row g-3 mb-4">
                                <div class="col-12 col-sm-6">
                                    <label for="status" class="form-label fw-semibold">Project Status <span class="text-danger">*</span></label>
                                    <select class="form-select" id="status" name="status" required>
                                        <c:forEach var="s" items="${statuses}">
                                            <option value="${s.name()}" ${(isEdit && project.status == s) ? 'selected' : ''}>
                                                ${s.displayName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <c:if test="${sessionScope.currentUser.admin}">
                                    <div class="col-12 col-sm-6">
                                        <label for="managerId" class="form-label fw-semibold">Assigned Project Manager <span class="text-danger">*</span></label>
                                        <select class="form-select" id="managerId" name="managerId" required>
                                            <c:forEach var="m" items="${managers}">
                                                <option value="${m.id}" ${(isEdit && project.managerId == m.id) ? 'selected' : ''}>
                                                    ${m.name} (${m.email})
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </c:if>
                            </div>

                            <c:if test="${!isEdit && not empty availableMembers}">
                                <div class="mb-4">
                                    <label class="form-label fw-semibold">Select Initial Team Members</label>
                                    <div class="border rounded p-3 bg-light" style="max-height: 180px; overflow-y: auto;">
                                        <c:forEach var="mem" items="${availableMembers}">
                                            <div class="form-check mb-2">
                                                <input class="form-check-input" type="checkbox" name="memberIds" value="${mem.id}" id="member_${mem.id}">
                                                <label class="form-check-label small" for="member_${mem.id}">
                                                    <strong>${mem.name}</strong> (${mem.email})
                                                </label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <div class="d-flex justify-content-end gap-2 border-top pt-3">
                                <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-secondary">Cancel</a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check2-circle me-1"></i> ${isEdit ? 'Update Project' : 'Create Project'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
