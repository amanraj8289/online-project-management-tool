package com.projectmanagement.filter;

import com.projectmanagement.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication Filter enforcing session-based access control.
 * Unauthenticated requests to protected endpoints are redirected to the login page.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        // Allow static assets
        if (path.startsWith("/assets/") || path.endsWith(".css") || path.endsWith(".js") ||
                path.endsWith(".ico") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // Allow public pages
        if (path.equals("/login") || path.equals("/logout") || path.equals("/") || path.equals("/index.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Check active session
        HttpSession session = req.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            String redirectUrl = req.getRequestURI();
            if (req.getQueryString() != null) {
                redirectUrl += "?" + req.getQueryString();
            }
            req.getSession(true).setAttribute("redirectAfterLogin", redirectUrl);
            res.sendRedirect(contextPath + "/login?error=session_expired");
            return;
        }

        chain.doFilter(request, response);
    }
}
