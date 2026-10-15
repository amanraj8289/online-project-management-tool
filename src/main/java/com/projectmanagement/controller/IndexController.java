package com.projectmanagement.controller;

import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Root Controller directing root URL visits to the appropriate dashboard or login.
 */
@WebServlet(name = "IndexController", urlPatterns = {""})
public class IndexController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            Role role = user.getRole();
            if (role == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else if (role == Role.PROJECT_MANAGER) {
                resp.sendRedirect(req.getContextPath() + "/manager/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/member/dashboard");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
