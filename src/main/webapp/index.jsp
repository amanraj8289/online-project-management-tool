<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    com.projectmanagement.model.User user = (com.projectmanagement.model.User) session.getAttribute("currentUser");
    if (user != null) {
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (user.isProjectManager()) {
            response.sendRedirect(request.getContextPath() + "/manager/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/member/dashboard");
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
