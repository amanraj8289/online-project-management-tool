package com.projectmanagement.filter;

import com.projectmanagement.util.CsrfTokenUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Anti-CSRF Filter validating CSRF tokens on mutating HTTP requests.
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = "/*")
public class CsrfFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Auto-generate/ensure CSRF token exists for any valid session
        if (req.getSession(false) != null) {
            CsrfTokenUtil.getOrCreateToken(req.getSession(false));
        }

        String method = req.getMethod();
        // Safe idempotent methods
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Skip CSRF validation for login submission (pre-session creation) and API chart endpoints
        if (path.equals("/login") || path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // Validate CSRF token for state-changing requests
        if (!CsrfTokenUtil.isValidToken(req)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("errorMessage", "Invalid or missing CSRF token. Request blocked for security.");
            req.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }
}
