package com.projectmanagement.dao;

import com.projectmanagement.model.Project;
import com.projectmanagement.model.dto.ProjectProgressDTO;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object Interface for Project management persistence.
 */
public interface ProjectDAO {

    Optional<Project> findById(Long id);

    List<Project> findAll();

    List<Project> findByManagerId(Long managerId);

    List<Project> findByMemberUserId(Long userId);

    Project create(Project project);

    boolean update(Project project);

    boolean delete(Long id);

    int countTotalProjects();

    int countActiveProjects();

    int countCompletedProjects();

    int countManagerProjects(Long managerId);

    List<ProjectProgressDTO> getProjectsProgressOverview();

    List<ProjectProgressDTO> getManagerProjectsProgressOverview(Long managerId);
}
