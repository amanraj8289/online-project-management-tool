package com.projectmanagement.service;

import com.projectmanagement.model.dto.DashboardMetricsDTO;
import com.projectmanagement.model.dto.ProjectProgressDTO;

import java.util.List;

/**
 * Service Interface for aggregating KPI metrics and Chart.js analytics.
 */
public interface DashboardService {

    DashboardMetricsDTO getAdminDashboardMetrics();

    DashboardMetricsDTO getManagerDashboardMetrics(Long managerId);

    DashboardMetricsDTO getMemberDashboardMetrics(Long userId);

    List<ProjectProgressDTO> getProjectsProgress();

    List<ProjectProgressDTO> getManagerProjectsProgress(Long managerId);
}
