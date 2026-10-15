package com.projectmanagement.dao;

import com.projectmanagement.model.ProjectMember;
import com.projectmanagement.model.User;

import java.util.List;

/**
 * Data Access Object Interface for Project Member mappings.
 */
public interface ProjectMemberDAO {

    List<ProjectMember> findMembersByProjectId(Long projectId);

    List<User> findUsersNotAssignedToProject(Long projectId);

    boolean addMember(Long projectId, Long userId);

    boolean removeMember(Long projectId, Long userId);

    boolean isUserMemberOfProject(Long projectId, Long userId);

    int countMembersByProjectId(Long projectId);
}
