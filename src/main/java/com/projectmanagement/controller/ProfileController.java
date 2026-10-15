package com.projectmanagement.controller;

import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.model.User;
import com.projectmanagement.service.ActivityLogService;
import com.projectmanagement.service.ServiceFactory;
import com.projectmanagement.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Controller for User Profile management and secure Password changing.
 */
@WebServlet(name = "ProfileController", urlPatterns = {"/profile", "/profile/update", "/profile/password"})
public class ProfileController extends HttpServlet {

    private final UserService userService = ServiceFactory.getUserService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        User freshUser = userService.getUserById(currentUser.getId());
        req.setAttribute("user", freshUser);
        req.getRequestDispatcher("/WEB-INF/views/profile/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        String path = req.getServletPath();

        if ("/profile/update".equals(path)) {
            String name = req.getParameter("name");
            String email = req.getParameter("email");

            try {
                userService.updateProfile(currentUser.getId(), name, email);
                currentUser.setName(name);
                currentUser.setEmail(email);
                session.setAttribute("currentUser", currentUser);

                activityLogService.logActivity(currentUser.getId(), null, null, "PROFILE_UPDATED", "Updated personal profile information.");
                resp.sendRedirect(req.getContextPath() + "/profile?success=Profile updated successfully");
            } catch (ValidationException e) {
                resp.sendRedirect(req.getContextPath() + "/profile?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/profile/password".equals(path)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            if (newPassword == null || !newPassword.equals(confirmPassword)) {
                resp.sendRedirect(req.getContextPath() + "/profile?error=New passwords do not match");
                return;
            }

            try {
                userService.changePassword(currentUser.getId(), currentPassword, newPassword);
                activityLogService.logActivity(currentUser.getId(), null, null, "PASSWORD_CHANGED", "Changed account password.");
                resp.sendRedirect(req.getContextPath() + "/profile?success=Password changed successfully");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/profile?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
        }
    }
}
