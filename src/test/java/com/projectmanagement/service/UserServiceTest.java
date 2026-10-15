package com.projectmanagement.service;

import com.projectmanagement.config.PasswordUtils;
import com.projectmanagement.dao.UserDAO;
import com.projectmanagement.exception.AuthenticationException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.model.AccountStatus;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import com.projectmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDAO mockUserDAO;
    private UserService userService;
    private final Map<Long, User> userDatabase = new HashMap<>();

    @BeforeEach
    void setUp() {
        userDatabase.clear();

        // Stub in-memory UserDAO implementation
        mockUserDAO = new UserDAO() {
            private long nextId = 1L;

            @Override
            public Optional<User> findById(Long id) {
                return Optional.ofNullable(userDatabase.get(id));
            }

            @Override
            public Optional<User> findByEmail(String email) {
                return userDatabase.values().stream()
                        .filter(u -> u.getEmail().equalsIgnoreCase(email))
                        .findFirst();
            }

            @Override
            public List<User> findAll(int offset, int limit, String searchKeyword) {
                return new ArrayList<>(userDatabase.values());
            }

            @Override
            public int countAll(String searchKeyword) {
                return userDatabase.size();
            }

            @Override
            public List<User> findByRole(Role role) {
                return userDatabase.values().stream().filter(u -> u.getRole() == role).toList();
            }

            @Override
            public List<User> findAllActive() {
                return userDatabase.values().stream().filter(u -> u.getAccountStatus() == AccountStatus.ACTIVE).toList();
            }

            @Override
            public User create(User user) {
                user.setId(nextId++);
                userDatabase.put(user.getId(), user);
                return user;
            }

            @Override
            public boolean update(User user) {
                userDatabase.put(user.getId(), user);
                return true;
            }

            @Override
            public boolean updatePassword(Long userId, String newPasswordHash) {
                User u = userDatabase.get(userId);
                if (u != null) {
                    u.setPasswordHash(newPasswordHash);
                    return true;
                }
                return false;
            }

            @Override
            public boolean updateProfile(Long userId, String name, String email) {
                User u = userDatabase.get(userId);
                if (u != null) {
                    u.setName(name);
                    u.setEmail(email);
                    return true;
                }
                return false;
            }

            @Override
            public boolean delete(Long id) {
                return userDatabase.remove(id) != null;
            }

            @Override
            public int countActiveAdmins() {
                return (int) userDatabase.values().stream()
                        .filter(u -> u.getRole() == Role.ADMIN && u.getAccountStatus() == AccountStatus.ACTIVE)
                        .count();
            }

            @Override
            public boolean isEmailTaken(String email, Long excludeUserId) {
                return userDatabase.values().stream()
                        .anyMatch(u -> u.getEmail().equalsIgnoreCase(email) && !u.getId().equals(excludeUserId));
            }

            @Override
            public int countTotalUsers() {
                return userDatabase.size();
            }
        };

        userService = new UserServiceImpl(mockUserDAO);

        // Preload standard users
        User admin = new User(1L, "Admin User", "admin@promanage.com", PasswordUtils.hashPassword("Admin@123"), Role.ADMIN, AccountStatus.ACTIVE);
        User pm = new User(2L, "Sarah PM", "sarah@promanage.com", PasswordUtils.hashPassword("Manager@123"), Role.PROJECT_MANAGER, AccountStatus.ACTIVE);
        User member = new User(3L, "Alex Dev", "alex@promanage.com", PasswordUtils.hashPassword("Member@123"), Role.TEAM_MEMBER, AccountStatus.ACTIVE);

        userDatabase.put(admin.getId(), admin);
        userDatabase.put(pm.getId(), pm);
        userDatabase.put(member.getId(), member);
    }

    @Test
    @DisplayName("Test successful authentication with valid credentials")
    void testSuccessfulAuthentication() {
        User authenticated = userService.authenticate("admin@promanage.com", "Admin@123");
        assertNotNull(authenticated);
        assertEquals("admin@promanage.com", authenticated.getEmail());
        assertTrue(authenticated.isAdmin());
    }

    @Test
    @DisplayName("Test authentication failure with incorrect password")
    void testFailedAuthenticationWrongPassword() {
        assertThrows(AuthenticationException.class, () ->
                userService.authenticate("admin@promanage.com", "WrongPassword!"));
    }

    @Test
    @DisplayName("Test authentication failure for inactive account")
    void testInactiveAccountAuthentication() {
        User inactive = new User(4L, "Inactive User", "inactive@promanage.com",
                PasswordUtils.hashPassword("Pass@123"), Role.TEAM_MEMBER, AccountStatus.INACTIVE);
        userDatabase.put(inactive.getId(), inactive);

        assertThrows(AuthenticationException.class, () ->
                userService.authenticate("inactive@promanage.com", "Pass@123"));
    }

    @Test
    @DisplayName("Test safeguard: Prevent logged-in admin from deleting own account")
    void testPreventSelfDeletion() {
        assertThrows(ValidationException.class, () ->
                userService.deleteUser(1L, 1L));
    }

    @Test
    @DisplayName("Test safeguard: Prevent deleting the last active administrator")
    void testPreventDeletingLastAdmin() {
        // There is only 1 admin in userDatabase (id 1)
        assertThrows(ValidationException.class, () ->
                userService.deleteUser(1L, 2L));
    }

    @Test
    @DisplayName("Test duplicate email prevention during user creation")
    void testDuplicateEmailRejection() {
        User duplicate = new User(null, "Duplicate Guy", "admin@promanage.com", null, Role.TEAM_MEMBER, AccountStatus.ACTIVE);
        assertThrows(ValidationException.class, () ->
                userService.createUser(duplicate, "Secret123"));
    }
}
