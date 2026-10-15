package com.projectmanagement.service;

import com.projectmanagement.model.ActivityLog;

import java.util.List;

public interface ActivityLogService {

    void logActivity(Long userId, Long projectId, Long taskId, String actionType, String description);

    List<ActivityLog> getRecentLogs(int limit);

    List<ActivityLog> getUserLogs(Long userId, int limit);

    List<ActivityLog> getProjectLogs(Long projectId, int limit);
}
