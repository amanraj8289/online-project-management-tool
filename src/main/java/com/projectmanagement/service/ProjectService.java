package com.projectmanagement.service;

import com.projectmanagement.model.Project;
import com.projectmanagement.model.ProjectMember;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;

import java.util.List;

/**
 * Service Interface for Project management and team assignment operations.
 */
public interface ProjectService {

    Project getProjectById(Long id);

    List<Project> getAllProjects();

    List<Project> getProjectsByManager(Long managerId);

    List<Project> getProjectsForMember(Long userId);

    Project createProject(Project project, List<Long> memberIds);

    void updateProject(Project project, Long managerId, boolean isAdmin, List<Long> memberIds);

    void deleteProject(Long projectId, Long managerId, boolean isAdmin);

    List<ProjectMember> getProjectMembers(Long projectId);

    List<User> getAvailableUsersForProject(Long projectId);

    void addMemberToProject(Long projectId, Long userId, Long currentUserId, boolean isAdmin);

    void removeMemberFromProject(Long projectId, Long userId, Long currentUserId, boolean isAdmin);

    boolean isUserAuthorizedForProject(Long projectId, Long userId, Role userRole);
}
