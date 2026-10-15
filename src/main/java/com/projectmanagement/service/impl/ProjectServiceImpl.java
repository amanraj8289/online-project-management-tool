package com.projectmanagement.service.impl;

import com.projectmanagement.dao.ProjectDAO;
import com.projectmanagement.dao.ProjectMemberDAO;
import com.projectmanagement.exception.AuthorizationException;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.ProjectMember;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import com.projectmanagement.service.ProjectService;
import com.projectmanagement.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectDAO projectDAO;
    private final ProjectMemberDAO projectMemberDAO;

    public ProjectServiceImpl(ProjectDAO projectDAO, ProjectMemberDAO projectMemberDAO) {
        this.projectDAO = projectDAO;
        this.projectMemberDAO = projectMemberDAO;
    }

    @Override
    public Project getProjectById(Long id) {
        return projectDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    @Override
    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    @Override
    public List<Project> getProjectsByManager(Long managerId) {
        return projectDAO.findByManagerId(managerId);
    }

    @Override
    public List<Project> getProjectsForMember(Long userId) {
        return projectDAO.findByMemberUserId(userId);
    }

    @Override
    public Project createProject(Project project, List<Long> memberIds) {
        ValidationUtils.validateProject(project.getTitle(), project.getStartDate(), project.getDueDate());

        Project created = projectDAO.create(project);

        if (memberIds != null) {
            for (Long memberId : memberIds) {
                if (memberId != null && !memberId.equals(project.getManagerId())) {
                    projectMemberDAO.addMember(created.getId(), memberId);
                }
            }
        }

        logger.info("Project created: {} (ID: {}) by Manager: {}", created.getTitle(), created.getId(), created.getManagerId());
        return created;
    }

    @Override
    public void updateProject(Project project, Long currentUserId, boolean isAdmin, List<Long> memberIds) {
        ValidationUtils.validateProject(project.getTitle(), project.getStartDate(), project.getDueDate());

        Project existing = getProjectById(project.getId());
        if (!isAdmin && !existing.getManagerId().equals(currentUserId)) {
            throw new AuthorizationException("You are not authorized to modify projects managed by another user.");
        }

        boolean updated = projectDAO.update(project);
        if (!updated) {
            throw new ResourceNotFoundException("Project", project.getId());
        }

        if (memberIds != null) {
            // Synchronize members
            List<ProjectMember> existingMembers = projectMemberDAO.findMembersByProjectId(project.getId());
            for (ProjectMember pm : existingMembers) {
                if (!memberIds.contains(pm.getUserId())) {
                    projectMemberDAO.removeMember(project.getId(), pm.getUserId());
                }
            }
            for (Long mId : memberIds) {
                if (mId != null && !projectMemberDAO.isUserMemberOfProject(project.getId(), mId)) {
                    projectMemberDAO.addMember(project.getId(), mId);
                }
            }
        }

        logger.info("Project updated: {} (ID: {})", project.getTitle(), project.getId());
    }

    @Override
    public void deleteProject(Long projectId, Long currentUserId, boolean isAdmin) {
        Project existing = getProjectById(projectId);
        if (!isAdmin && !existing.getManagerId().equals(currentUserId)) {
            throw new AuthorizationException("You are not authorized to delete projects managed by another user.");
        }

        boolean deleted = projectDAO.delete(projectId);
        if (!deleted) {
            throw new ResourceNotFoundException("Project", projectId);
        }
        logger.info("Project ID {} deleted by User ID {}", projectId, currentUserId);
    }

    @Override
    public List<ProjectMember> getProjectMembers(Long projectId) {
        return projectMemberDAO.findMembersByProjectId(projectId);
    }

    @Override
    public List<User> getAvailableUsersForProject(Long projectId) {
        return projectMemberDAO.findUsersNotAssignedToProject(projectId);
    }

    @Override
    public void addMemberToProject(Long projectId, Long userId, Long currentUserId, boolean isAdmin) {
        Project project = getProjectById(projectId);
        if (!isAdmin && !project.getManagerId().equals(currentUserId)) {
            throw new AuthorizationException("You cannot add members to projects you do not manage.");
        }
        if (!projectMemberDAO.isUserMemberOfProject(projectId, userId)) {
            projectMemberDAO.addMember(projectId, userId);
            logger.info("User {} added to project {}", userId, projectId);
        }
    }

    @Override
    public void removeMemberFromProject(Long projectId, Long userId, Long currentUserId, boolean isAdmin) {
        Project project = getProjectById(projectId);
        if (!isAdmin && !project.getManagerId().equals(currentUserId)) {
            throw new AuthorizationException("You cannot remove members from projects you do not manage.");
        }
        projectMemberDAO.removeMember(projectId, userId);
        logger.info("User {} removed from project {}", userId, projectId);
    }

    @Override
    public boolean isUserAuthorizedForProject(Long projectId, Long userId, Role userRole) {
        if (userRole == Role.ADMIN) {
            return true;
        }
        Project project = getProjectById(projectId);
        if (userRole == Role.PROJECT_MANAGER) {
            return project.getManagerId().equals(userId);
        }
        if (userRole == Role.TEAM_MEMBER) {
            return projectMemberDAO.isUserMemberOfProject(projectId, userId);
        }
        return false;
    }
}
