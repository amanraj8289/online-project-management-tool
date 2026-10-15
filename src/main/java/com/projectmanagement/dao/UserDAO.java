package com.projectmanagement.dao;

import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object Interface for User persistence operations.
 */
public interface UserDAO {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll(int offset, int limit, String searchKeyword);

    int countAll(String searchKeyword);

    List<User> findByRole(Role role);

    List<User> findAllActive();

    User create(User user);

    boolean update(User user);

    boolean updatePassword(Long userId, String newPasswordHash);

    boolean updateProfile(Long userId, String name, String email);

    boolean delete(Long id);

    int countActiveAdmins();

    boolean isEmailTaken(String email, Long excludeUserId);

    int countTotalUsers();
}
