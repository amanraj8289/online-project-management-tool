package com.projectmanagement.filter;

import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Role-Based Access Control (RBAC) Filter.
 * Enforces server-side authorization based on user roles (ADMIN, PROJECT_MANAGER, TEAM_MEMBER).
 */
@WebFilter(filterName = "RoleAuthorizationFilter", urlPatterns = "/*")
public class RoleAuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        HttpSession session = req.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        if (currentUser != null) {
            Role role = currentUser.getRole();

            // Admin only routes
            if (path.startsWith("/admin/") || path.startsWith("/users/") || path.startsWith("/settings/")) {
                if (role != Role.ADMIN) {
                    res.sendRedirect(contextPath + getDashboardUrlForRole(role) + "?error=unauthorized");
                    return;
                }
            }

            // Project Manager routes (Admin also has access)
            if (path.startsWith("/manager/")) {
                if (role != Role.ADMIN && role != Role.PROJECT_MANAGER) {
                    res.sendRedirect(contextPath + getDashboardUrlForRole(role) + "?error=unauthorized");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private String getDashboardUrlForRole(Role role) {
        if (role == Role.ADMIN) return "/admin/dashboard";
        if (role == Role.PROJECT_MANAGER) return "/manager/dashboard";
        return "/member/dashboard";
    }
}
