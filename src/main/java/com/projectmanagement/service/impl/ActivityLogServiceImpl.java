package com.projectmanagement.service.impl;

import com.projectmanagement.dao.ActivityLogDAO;
import com.projectmanagement.model.ActivityLog;
import com.projectmanagement.service.ActivityLogService;
import com.projectmanagement.util.BackgroundJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivityLogServiceImpl implements ActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogServiceImpl.class);
    private final ActivityLogDAO activityLogDAO;

    public ActivityLogServiceImpl(ActivityLogDAO activityLogDAO) {
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public void logActivity(Long userId, Long projectId, Long taskId, String actionType, String description) {
        if (userId == null) return;
        // Asynchronous background audit logging
        BackgroundJobExecutor.execute(() -> {
            try {
                ActivityLog log = new ActivityLog(userId, projectId, taskId, actionType, description);
                activityLogDAO.create(log);
            } catch (Exception e) {
                logger.error("Failed to asynchronously persist activity log: {}", e.getMessage());
            }
        });
    }

    @Override
    public List<ActivityLog> getRecentLogs(int limit) {
        return activityLogDAO.findRecentLogs(limit);
    }

    @Override
    public List<ActivityLog> getUserLogs(Long userId, int limit) {
        return activityLogDAO.findByUserId(userId, limit);
    }

    @Override
    public List<ActivityLog> getProjectLogs(Long projectId, int limit) {
        return activityLogDAO.findByProjectId(projectId, limit);
    }
}
