<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="isEdit" value="${not empty task}" />
<c:set var="pageTitle" value="${isEdit ? 'Edit Task' : 'Create New Task'}" />
<c:set var="activeNav" value="tasks" />
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
                            <i class="bi ${isEdit ? 'bi-pencil-square' : 'bi-plus-square'} text-primary fs-5"></i>
                            <h5 class="card-title mb-0 fw-bold">${isEdit ? 'Update Task Details' : 'Assign New Task'}</h5>
                        </div>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/tasks/${isEdit ? 'edit' : 'create'}" method="post" class="needs-validation" novalidate>
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />
                            <c:if test="${isEdit}">
                                <input type="hidden" name="id" value="${task.id}" />
                            </c:if>

                            <div class="mb-3">
                                <label for="projectId" class="form-label fw-semibold">Associated Project <span class="text-danger">*</span></label>
                                <select class="form-select" id="projectId" name="projectId" required>
                                    <option value="">Select project...</option>
                                    <c:forEach var="p" items="${projects}">
                                        <option value="${p.id}" ${(isEdit && task.projectId == p.id) || selectedProjectId == p.id ? 'selected' : ''}>
                                            <c:out value="${p.title}" />
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">Please choose a project.</div>
                            </div>

                            <div class="mb-3">
                                <label for="title" class="form-label fw-semibold">Task Title <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title"
                                       value="<c:out value='${isEdit ? task.title : title}' />" required maxlength="255">
                                <div class="invalid-feedback">Task title is required.</div>
                            </div>

                            <div class="mb-3">
                                <label for="description" class="form-label fw-semibold">Task Description</label>
                                <textarea class="form-control" id="description" name="description" rows="3"><c:out value="${isEdit ? task.description : description}" /></textarea>
                            </div>

                            <div class="row g-3 mb-3">
                                <div class="col-12 col-sm-6">
                                    <label for="assignedTo" class="form-label fw-semibold">Assign Team Member</label>
                                    <select class="form-select" id="assignedTo" name="assignedTo">
                                        <option value="">Unassigned</option>
                                        <c:forEach var="u" items="${allUsers}">
                                            <option value="${u.id}" ${(isEdit && task.assignedTo == u.id) ? 'selected' : ''}>
                                                <c:out value="${u.name}" /> (${u.role.displayName})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="col-12 col-sm-6">
                                    <label for="priority" class="form-label fw-semibold">Priority Level <span class="text-danger">*</span></label>
                                    <select class="form-select" id="priority" name="priority" required>
                                        <c:forEach var="pr" items="${priorities}">
                                            <option value="${pr.name()}" ${(isEdit && task.priority == pr) ? 'selected' : ''}>
                                                ${pr.displayName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="row g-3 mb-3">
                                <div class="col-12 col-sm-4">
                                    <label for="status" class="form-label fw-semibold">Status <span class="text-danger">*</span></label>
                                    <select class="form-select" id="status" name="status" required>
                                        <c:forEach var="st" items="${statuses}">
                                            <option value="${st.name()}" ${(isEdit && task.status == st) ? 'selected' : ''}>
                                                ${st.displayName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="col-12 col-sm-4">
                                    <label for="startDate" class="form-label fw-semibold">Start Date <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="startDate" name="startDate"
                                           value="${isEdit ? task.startDate : startDate}" required>
                                    <div class="invalid-feedback">Start date is required.</div>
                                </div>

                                <div class="col-12 col-sm-4">
                                    <label for="dueDate" class="form-label fw-semibold">Due Date <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="dueDate" name="dueDate"
                                           value="${isEdit ? task.dueDate : dueDate}" required>
                                    <div class="invalid-feedback">Due date is required.</div>
                                </div>
                            </div>

                            <div class="d-flex justify-content-end gap-2 border-top pt-3">
                                <a href="${pageContext.request.contextPath}/tasks" class="btn btn-outline-secondary">Cancel</a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check2-circle me-1"></i> ${isEdit ? 'Update Task' : 'Assign Task'}
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
