package com.projectmanagement.service;

import com.projectmanagement.dao.ProjectDAO;
import com.projectmanagement.dao.ProjectMemberDAO;
import com.projectmanagement.exception.AuthorizationException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.ProjectMember;
import com.projectmanagement.model.ProjectStatus;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import com.projectmanagement.model.dto.ProjectProgressDTO;
import com.projectmanagement.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectServiceTest {

    private ProjectService projectService;
    private final Map<Long, Project> projectDb = new HashMap<>();
    private final List<ProjectMember> memberDb = new ArrayList<>();

    @BeforeEach
    void setUp() {
        projectDb.clear();
        memberDb.clear();

        ProjectDAO mockProjectDAO = new ProjectDAO() {
            private long nextId = 1L;

            @Override
            public Optional<Project> findById(Long id) {
                return Optional.ofNullable(projectDb.get(id));
            }

            @Override
            public List<Project> findAll() {
                return new ArrayList<>(projectDb.values());
            }

            @Override
            public List<Project> findByManagerId(Long managerId) {
                return projectDb.values().stream().filter(p -> p.getManagerId().equals(managerId)).toList();
            }

            @Override
            public List<Project> findByMemberUserId(Long userId) {
                return Collections.emptyList();
            }

            @Override
            public Project create(Project project) {
                project.setId(nextId++);
                projectDb.put(project.getId(), project);
                return project;
            }

            @Override
            public boolean update(Project project) {
                projectDb.put(project.getId(), project);
                return true;
            }

            @Override
            public boolean delete(Long id) {
                return projectDb.remove(id) != null;
            }

            @Override
            public int countTotalProjects() {
                return projectDb.size();
            }

            @Override
            public int countActiveProjects() {
                return (int) projectDb.values().stream().filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS).count();
            }

            @Override
            public int countCompletedProjects() {
                return (int) projectDb.values().stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count();
            }

            @Override
            public int countManagerProjects(Long managerId) {
                return findByManagerId(managerId).size();
            }

            @Override
            public List<ProjectProgressDTO> getProjectsProgressOverview() {
                return Collections.emptyList();
            }

            @Override
            public List<ProjectProgressDTO> getManagerProjectsProgressOverview(Long managerId) {
                return Collections.emptyList();
            }
        };

        ProjectMemberDAO mockMemberDAO = new ProjectMemberDAO() {
            @Override
            public List<ProjectMember> findMembersByProjectId(Long projectId) {
                return memberDb.stream().filter(m -> m.getProjectId().equals(projectId)).toList();
            }

            @Override
            public List<User> findUsersNotAssignedToProject(Long projectId) {
                return Collections.emptyList();
            }

            @Override
            public boolean addMember(Long projectId, Long userId) {
                memberDb.add(new ProjectMember(projectId, userId));
                return true;
            }

            @Override
            public boolean removeMember(Long projectId, Long userId) {
                return memberDb.removeIf(m -> m.getProjectId().equals(projectId) && m.getUserId().equals(userId));
            }

            @Override
            public boolean isUserMemberOfProject(Long projectId, Long userId) {
                return memberDb.stream().anyMatch(m -> m.getProjectId().equals(projectId) && m.getUserId().equals(userId));
            }

            @Override
            public int countMembersByProjectId(Long projectId) {
                return findMembersByProjectId(projectId).size();
            }
        };

        projectService = new ProjectServiceImpl(mockProjectDAO, mockMemberDAO);

        // Preload sample project owned by manager ID 2
        Project p1 = new Project(1L, "Cloud Portal", "Cloud migration",
                Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusMonths(2)),
                ProjectStatus.IN_PROGRESS, 2L);
        projectDb.put(p1.getId(), p1);
    }

    @Test
    @DisplayName("Test project creation with valid dates")
    void testCreateProjectSuccess() {
        Project newProj = new Project(null, "AI Pipeline", "Predictive Analytics",
                Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusMonths(3)),
                ProjectStatus.PLANNING, 2L);

        Project created = projectService.createProject(newProj, List.of(3L, 4L));
        assertNotNull(created.getId());
        assertEquals("AI Pipeline", created.getTitle());
    }

    @Test
    @DisplayName("Test project authorization: Unauthorized manager cannot modify project of another manager")
    void testUnauthorizedManagerProjectModification() {
        Project existing = projectService.getProjectById(1L); // Owned by manager ID 2

        // Manager ID 99 tries to update it
        assertThrows(AuthorizationException.class, () ->
                projectService.updateProject(existing, 99L, false, Collections.emptyList()));
    }

    @Test
    @DisplayName("Test admin override: Admin can modify any project")
    void testAdminCanModifyAnyProject() {
        Project existing = projectService.getProjectById(1L);
        existing.setDescription("Updated by admin");

        assertDoesNotThrow(() ->
                projectService.updateProject(existing, 999L, true, Collections.emptyList()));
    }
}
