package com.projectmanagement.service;

import com.projectmanagement.dao.*;
import com.projectmanagement.dao.impl.*;
import com.projectmanagement.service.impl.*;

/**
 * Service Factory providing decoupled singleton service instances.
 */
public final class ServiceFactory {

    private static final UserDAO userDAO = new UserDAOImpl();
    private static final ProjectDAO projectDAO = new ProjectDAOImpl();
    private static final ProjectMemberDAO projectMemberDAO = new ProjectMemberDAOImpl();
    private static final TaskDAO taskDAO = new TaskDAOImpl();
    private static final TaskCommentDAO taskCommentDAO = new TaskCommentDAOImpl();
    private static final ActivityLogDAO activityLogDAO = new ActivityLogDAOImpl();
    private static final SystemSettingsDAO systemSettingsDAO = new SystemSettingsDAOImpl();
    private static final NotificationDAO notificationDAO = new NotificationDAOImpl();

    private static final UserService userService = new UserServiceImpl(userDAO);
    private static final ProjectService projectService = new ProjectServiceImpl(projectDAO, projectMemberDAO);
    private static final TaskService taskService = new TaskServiceImpl(taskDAO, projectDAO, taskCommentDAO);
    private static final ActivityLogService activityLogService = new ActivityLogServiceImpl(activityLogDAO);
    private static final NotificationService notificationService = new NotificationServiceImpl(notificationDAO);
    private static final SystemSettingsService systemSettingsService = new SystemSettingsServiceImpl(systemSettingsDAO);
    private static final DashboardService dashboardService = new DashboardServiceImpl(userDAO, projectDAO, taskDAO);

    private ServiceFactory() {
    }

    public static UserService getUserService() {
        return userService;
    }

    public static ProjectService getProjectService() {
        return projectService;
    }

    public static TaskService getTaskService() {
        return taskService;
    }

    public static ActivityLogService getActivityLogService() {
        return activityLogService;
    }

    public static NotificationService getNotificationService() {
        return notificationService;
    }

    public static SystemSettingsService getSystemSettingsService() {
        return systemSettingsService;
    }

    public static DashboardService getDashboardService() {
        return dashboardService;
    }
}
