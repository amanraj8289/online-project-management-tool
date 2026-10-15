package com.projectmanagement.controller;

import com.projectmanagement.model.Notification;
import com.projectmanagement.model.User;
import com.projectmanagement.service.NotificationService;
import com.projectmanagement.service.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for User Notifications management.
 */
@WebServlet(name = "NotificationController", urlPatterns = {"/notifications", "/notifications/read", "/notifications/read-all"})
public class NotificationController extends HttpServlet {

    private final NotificationService notificationService = ServiceFactory.getNotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        List<Notification> notifications = notificationService.getUserNotifications(currentUser.getId(), 50);
        req.setAttribute("notifications", notifications);

        req.getRequestDispatcher("/WEB-INF/views/common/notifications.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        String path = req.getServletPath();

        if ("/notifications/read".equals(path)) {
            Long id = Long.parseLong(req.getParameter("id"));
            notificationService.markAsRead(id, currentUser.getId());
        } else if ("/notifications/read-all".equals(path)) {
            notificationService.markAllAsRead(currentUser.getId());
        }

        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            resp.sendRedirect(referer);
        } else {
            resp.sendRedirect(req.getContextPath() + "/notifications");
        }
    }
}
