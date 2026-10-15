package com.projectmanagement.controller;

import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.model.AccountStatus;
import com.projectmanagement.model.Role;
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
import java.util.List;

/**
 * Controller for Admin User Management CRUD operations.
 */
@WebServlet(name = "UserController", urlPatterns = {"/users", "/users/create", "/users/edit", "/users/delete"})
public class UserController extends HttpServlet {

    private final UserService userService = ServiceFactory.getUserService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/users/create".equals(path)) {
            req.setAttribute("roles", Role.values());
            req.setAttribute("statuses", AccountStatus.values());
            req.getRequestDispatcher("/WEB-INF/views/admin/user-form.jsp").forward(req, resp);
            return;
        }

        if ("/users/edit".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                User user = userService.getUserById(id);
                req.setAttribute("user", user);
                req.setAttribute("roles", Role.values());
                req.setAttribute("statuses", AccountStatus.values());
                req.getRequestDispatcher("/WEB-INF/views/admin/user-form.jsp").forward(req, resp);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/users?error=" + e.getMessage());
            }
            return;
        }

        // List users with search and pagination
        int page = 1;
        try {
            if (req.getParameter("page") != null) {
                page = Math.max(1, Integer.parseInt(req.getParameter("page")));
            }
        } catch (NumberFormatException ignored) {}

        String keyword = req.getParameter("search");
        List<User> users = userService.getAllUsers(page, PAGE_SIZE, keyword);
        int totalUsers = userService.getTotalUserCount(keyword);
        int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);

        req.setAttribute("users", users);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalCount", totalUsers);
        req.setAttribute("searchKeyword", keyword != null ? keyword : "");

        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        User currentAdmin = (User) session.getAttribute("currentUser");

        if ("/users/create".equals(path)) {
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String roleStr = req.getParameter("role");
            String statusStr = req.getParameter("accountStatus");

            try {
                User newUser = new User();
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setRole(Role.fromString(roleStr));
                newUser.setAccountStatus(AccountStatus.fromString(statusStr));

                User created = userService.createUser(newUser, password);
                activityLogService.logActivity(currentAdmin.getId(), null, null, "USER_CREATED",
                        "Admin created user: " + created.getName() + " (" + created.getEmail() + ")");

                resp.sendRedirect(req.getContextPath() + "/users?success=User created successfully");
            } catch (ValidationException e) {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("name", name);
                req.setAttribute("email", email);
                req.setAttribute("selectedRole", roleStr);
                req.setAttribute("selectedStatus", statusStr);
                req.setAttribute("roles", Role.values());
                req.setAttribute("statuses", AccountStatus.values());
                req.getRequestDispatcher("/WEB-INF/views/admin/user-form.jsp").forward(req, resp);
            }
            return;
        }

        if ("/users/edit".equals(path)) {
            Long id = Long.parseLong(req.getParameter("id"));
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String roleStr = req.getParameter("role");
            String statusStr = req.getParameter("accountStatus");

            try {
                User user = userService.getUserById(id);
                user.setName(name);
                user.setEmail(email);
                user.setRole(Role.fromString(roleStr));
                user.setAccountStatus(AccountStatus.fromString(statusStr));

                userService.updateUser(user);
                activityLogService.logActivity(currentAdmin.getId(), null, null, "USER_UPDATED",
                        "Admin updated user ID: " + id + " (" + user.getEmail() + ")");

                resp.sendRedirect(req.getContextPath() + "/users?success=User updated successfully");
            } catch (Exception e) {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("roles", Role.values());
                req.setAttribute("statuses", AccountStatus.values());
                doGet(req, resp);
            }
            return;
        }

        if ("/users/delete".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                userService.deleteUser(id, currentAdmin.getId());
                activityLogService.logActivity(currentAdmin.getId(), null, null, "USER_DELETED",
                        "Admin deleted user ID: " + id);
                resp.sendRedirect(req.getContextPath() + "/users?success=User deleted successfully");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/users?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
        }
    }
}
