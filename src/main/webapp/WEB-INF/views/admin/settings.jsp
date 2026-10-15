<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="System Settings" />
<c:set var="activeNav" value="settings" />
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
                            <i class="bi bi-sliders text-primary fs-5"></i>
                            <h5 class="card-title mb-0 fw-bold">Global System Settings</h5>
                        </div>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/settings" method="post">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}" />

                            <div class="mb-3">
                                <label for="app_name" class="form-label fw-semibold">Application Name</label>
                                <input type="text" class="form-control" id="app_name" name="app_name"
                                       value="<c:out value='${settings[\"app_name\"] != null ? settings[\"app_name\"] : \"ProManage Enterprise\"}' />" required>
                                <div class="form-text">Displayed on navigation bars, headers, and outgoing reports.</div>
                            </div>

                            <div class="mb-3">
                                <label for="company_name" class="form-label fw-semibold">Organization / University Name</label>
                                <input type="text" class="form-control" id="company_name" name="company_name"
                                       value="<c:out value='${settings[\"company_name\"] != null ? settings[\"company_name\"] : \"Enterprise Technologies Ltd.\"}' />">
                            </div>

                            <div class="row g-3 mb-3">
                                <div class="col-12 col-sm-6">
                                    <label for="default_task_priority" class="form-label fw-semibold">Default Task Priority</label>
                                    <select class="form-select" id="default_task_priority" name="default_task_priority">
                                        <option value="LOW" ${settings['default_task_priority'] == 'LOW' ? 'selected' : ''}>Low</option>
                                        <option value="MEDIUM" ${settings['default_task_priority'] == 'MEDIUM' || empty settings['default_task_priority'] ? 'selected' : ''}>Medium</option>
                                        <option value="HIGH" ${settings['default_task_priority'] == 'HIGH' ? 'selected' : ''}>High</option>
                                        <option value="CRITICAL" ${settings['default_task_priority'] == 'CRITICAL' ? 'selected' : ''}>Critical</option>
                                    </select>
                                </div>
                                <div class="col-12 col-sm-6">
                                    <label for="session_timeout_minutes" class="form-label fw-semibold">Session Timeout (Minutes)</label>
                                    <input type="number" class="form-control" id="session_timeout_minutes" name="session_timeout_minutes"
                                           value="${settings['session_timeout_minutes'] != null ? settings['session_timeout_minutes'] : 30}" min="5" max="1440">
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="system_announcement" class="form-label fw-semibold">System Announcement Banner</label>
                                <textarea class="form-control" id="system_announcement" name="system_announcement" rows="3"><c:out value="${settings['system_announcement']}" /></textarea>
                                <div class="form-text">Broadcast message visible across all user dashboards.</div>
                            </div>

                            <div class="d-flex justify-content-end gap-2 border-top pt-3">
                                <button type="reset" class="btn btn-outline-secondary">Reset</button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-save me-1"></i> Save Configuration
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
