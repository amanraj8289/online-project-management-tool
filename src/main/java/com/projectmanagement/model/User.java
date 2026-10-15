package com.projectmanagement.model;

/**
 * User Entity demonstrating Encapsulation with private fields,
 * full constructors, getters, setters, and role checking methods.
 */
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String passwordHash;
    private Role role;
    private AccountStatus accountStatus;

    public User() {
        super();
        this.role = Role.TEAM_MEMBER;
        this.accountStatus = AccountStatus.ACTIVE;
    }

    public User(Long id, String name, String email, String passwordHash, Role role, AccountStatus accountStatus) {
        super(id);
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : Role.TEAM_MEMBER;
        this.accountStatus = accountStatus != null ? accountStatus : AccountStatus.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    // Role helper methods for clean JSP/Servlet condition checks
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isProjectManager() {
        return this.role == Role.PROJECT_MANAGER;
    }

    public boolean isTeamMember() {
        return this.role == Role.TEAM_MEMBER;
    }

    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", accountStatus=" + accountStatus +
                '}';
    }
}
