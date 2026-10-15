package com.projectmanagement.dao;

import com.projectmanagement.model.ActivityLog;

import java.util.List;

/**
 * Data Access Object Interface for System Activity Audit Logs.
 */
public interface ActivityLogDAO {

    List<ActivityLog> findRecentLogs(int limit);

    List<ActivityLog> findByUserId(Long userId, int limit);

    List<ActivityLog> findByProjectId(Long projectId, int limit);

    ActivityLog create(ActivityLog log);
}
