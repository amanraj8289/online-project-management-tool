package com.projectmanagement.controller;

import com.projectmanagement.model.Task;
import com.projectmanagement.model.TaskPriority;
import com.projectmanagement.model.TaskStatus;
import com.projectmanagement.model.User;
import com.projectmanagement.service.ServiceFactory;
import com.projectmanagement.service.TaskService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Team Member "My Tasks" view with dynamic filters and searches.
 */
@WebServlet(name = "MyTasksController", urlPatterns = {"/member/my-tasks"})
public class MyTasksController extends HttpServlet {

    private final TaskService taskService = ServiceFactory.getTaskService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        String statusParam = req.getParameter("status");
        String priorityParam = req.getParameter("priority");
        String searchParam = req.getParameter("search");

        TaskStatus status = (statusParam != null && !statusParam.isEmpty()) ? TaskStatus.fromString(statusParam) : null;
        TaskPriority priority = (priorityParam != null && !priorityParam.isEmpty()) ? TaskPriority.fromString(priorityParam) : null;

        List<Task> tasks = taskService.getFilteredTasksForUser(currentUser.getId(), status, priority, searchParam);

        req.setAttribute("tasks", tasks);
        req.setAttribute("selectedStatus", statusParam);
        req.setAttribute("selectedPriority", priorityParam);
        req.setAttribute("searchKeyword", searchParam != null ? searchParam : "");
        req.setAttribute("statuses", TaskStatus.values());
        req.setAttribute("priorities", TaskPriority.values());

        req.getRequestDispatcher("/WEB-INF/views/member/my-tasks.jsp").forward(req, resp);
    }
}
