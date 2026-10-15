package com.projectmanagement.controller;

import com.projectmanagement.exception.AuthenticationException;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import com.projectmanagement.service.ActivityLogService;
import com.projectmanagement.service.ServiceFactory;
import com.projectmanagement.service.UserService;
import com.projectmanagement.util.CsrfTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication Controller handling Login, Logout, and Role-Based Redirection.
 */
@WebServlet(name = "AuthController", urlPatterns = {"/login", "/logout"})
public class AuthController extends HttpServlet {

    private final UserService userService = ServiceFactory.getUserService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            handleLogout(req, resp);
            return;
        }

        // Check if already logged in
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            redirectToDashboard(user, req, resp);
            return;
        }

        // Show login page
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            handleLogout(req, resp);
            return;
        }

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = userService.authenticate(email, password);

            // Invalidate existing session to prevent session fixation attacks
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create new fresh session
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("currentUser", user);
            newSession.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout
            CsrfTokenUtil.getOrCreateToken(newSession);

            activityLogService.logActivity(user.getId(), null, null, "USER_LOGIN",
                    "User logged in successfully from IP: " + req.getRemoteAddr());

            String redirectAfterLogin = (String) newSession.getAttribute("redirectAfterLogin");
            if (redirectAfterLogin != null && !redirectAfterLogin.isEmpty()) {
                newSession.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirectAfterLogin);
            } else {
                redirectToDashboard(user, req, resp);
            }

        } catch (AuthenticationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                activityLogService.logActivity(user.getId(), null, null, "USER_LOGOUT", "User logged out.");
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login?logout=success");
    }

    private void redirectToDashboard(User user, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Role role = user.getRole();
        if (role == Role.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else if (role == Role.PROJECT_MANAGER) {
            resp.sendRedirect(req.getContextPath() + "/manager/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/member/dashboard");
        }
    }
}
