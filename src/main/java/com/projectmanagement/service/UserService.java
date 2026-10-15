package com.projectmanagement.service;

import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;

import java.util.List;

/**
 * Service Interface for User authentication and account management.
 */
public interface UserService {

    User authenticate(String email, String plainPassword);

    User getUserById(Long id);

    User getUserByEmail(String email);

    List<User> getAllUsers(int page, int pageSize, String searchKeyword);

    int getTotalUserCount(String searchKeyword);

    List<User> getUsersByRole(Role role);

    List<User> getAllActiveUsers();

    User createUser(User user, String plainPassword);

    void updateUser(User user);

    void updateProfile(Long userId, String name, String email);

    void changePassword(Long userId, String currentPassword, String newPassword);

    void deleteUser(Long userId, Long currentAdminId);
}
