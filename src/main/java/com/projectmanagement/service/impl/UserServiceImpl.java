package com.projectmanagement.service.impl;

import com.projectmanagement.config.PasswordUtils;
import com.projectmanagement.dao.UserDAO;
import com.projectmanagement.exception.AuthenticationException;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.model.AccountStatus;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import com.projectmanagement.service.UserService;
import com.projectmanagement.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User authenticate(String email, String plainPassword) {
        if (email == null || plainPassword == null || email.trim().isEmpty() || plainPassword.trim().isEmpty()) {
            throw new AuthenticationException("Email and password are required.");
        }

        User user = userDAO.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AuthenticationException("Your account is currently inactive. Please contact an administrator.");
        }

        if (!PasswordUtils.verifyPassword(plainPassword, user.getPasswordHash())) {
            logger.warn("Failed login attempt for user: {}", email);
            throw new AuthenticationException("Invalid email or password.");
        }

        logger.info("User authenticated successfully: {} (Role: {})", user.getEmail(), user.getRole());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found."));
    }

    @Override
    public List<User> getAllUsers(int page, int pageSize, String searchKeyword) {
        int offset = Math.max(0, (page - 1) * pageSize);
        return userDAO.findAll(offset, pageSize, searchKeyword);
    }

    @Override
    public int getTotalUserCount(String searchKeyword) {
        return userDAO.countAll(searchKeyword);
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userDAO.findByRole(role);
    }

    @Override
    public List<User> getAllActiveUsers() {
        return userDAO.findAllActive();
    }

    @Override
    public User createUser(User user, String plainPassword) {
        ValidationUtils.validateUserRegistration(user.getName(), user.getEmail(), plainPassword, true);

        if (userDAO.isEmailTaken(user.getEmail(), null)) {
            throw new ValidationException("A user with email '" + user.getEmail() + "' already exists.");
        }

        String hashedPassword = PasswordUtils.hashPassword(plainPassword);
        user.setPasswordHash(hashedPassword);
        return userDAO.create(user);
    }

    @Override
    public void updateUser(User user) {
        ValidationUtils.validateUserRegistration(user.getName(), user.getEmail(), null, false);

        if (userDAO.isEmailTaken(user.getEmail(), user.getId())) {
            throw new ValidationException("Email '" + user.getEmail() + "' is already in use by another user.");
        }

        boolean updated = userDAO.update(user);
        if (!updated) {
            throw new ResourceNotFoundException("User", user.getId());
        }
    }

    @Override
    public void updateProfile(Long userId, String name, String email) {
        if (name == null || name.trim().length() < 2) {
            throw new ValidationException("Name must be at least 2 characters.");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            throw new ValidationException("A valid email address is required.");
        }

        if (userDAO.isEmailTaken(email, userId)) {
            throw new ValidationException("Email '" + email + "' is already in use by another account.");
        }

        boolean updated = userDAO.updateProfile(userId, name.trim(), email.trim().toLowerCase());
        if (!updated) {
            throw new ResourceNotFoundException("User", userId);
        }
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }

        User user = getUserById(userId);
        if (!PasswordUtils.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("Incorrect current password.");
        }

        String newHash = PasswordUtils.hashPassword(newPassword.trim());
        userDAO.updatePassword(userId, newHash);
        logger.info("Password changed successfully for user ID: {}", userId);
    }

    @Override
    public void deleteUser(Long userId, Long currentAdminId) {
        if (userId.equals(currentAdminId)) {
            throw new ValidationException("You cannot delete your own logged-in administrator account.");
        }

        User targetUser = getUserById(userId);
        if (targetUser.getRole() == Role.ADMIN) {
            int activeAdmins = userDAO.countActiveAdmins();
            if (activeAdmins <= 1) {
                throw new ValidationException("Cannot delete the last active administrator in the system.");
            }
        }

        boolean deleted = userDAO.delete(userId);
        if (!deleted) {
            throw new ResourceNotFoundException("User", userId);
        }
        logger.info("User ID {} deleted by Admin ID {}", userId, currentAdminId);
    }
}
