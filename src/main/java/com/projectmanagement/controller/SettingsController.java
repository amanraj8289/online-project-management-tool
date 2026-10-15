package com.projectmanagement.controller;

import com.projectmanagement.model.User;
import com.projectmanagement.service.ActivityLogService;
import com.projectmanagement.service.ServiceFactory;
import com.projectmanagement.service.SystemSettingsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for Admin System Settings configuration.
 */
@WebServlet(name = "SettingsController", urlPatterns = {"/settings"})
public class SettingsController extends HttpServlet {

    private final SystemSettingsService settingsService = ServiceFactory.getSystemSettingsService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> settings = settingsService.getSettings();
        req.setAttribute("settings", settings);
        req.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentAdmin = (User) session.getAttribute("currentUser");

        Map<String, String> updatedSettings = new HashMap<>();
        Enumeration<String> paramNames = req.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String param = paramNames.nextElement();
            if (!param.equals("_csrf") && !param.startsWith("submit")) {
                updatedSettings.put(param, req.getParameter(param));
            }
        }

        try {
            settingsService.updateSettings(updatedSettings);
            activityLogService.logActivity(currentAdmin.getId(), null, null, "SETTINGS_UPDATED",
                    "Admin updated global system settings.");

            resp.sendRedirect(req.getContextPath() + "/settings?success=System settings updated successfully");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/settings?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
