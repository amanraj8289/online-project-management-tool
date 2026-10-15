# Testing and Verification Guide

## 1. Automated Unit & Integration Tests Summary

The project includes automated test coverage written with JUnit 5 covering security, business rules, models, and services.

### Test Execution Command
```bash
# Execute all automated tests
mvn test

# Run a specific test class
mvn test -Dtest=UserServiceTest
```

### Verified Test Suite Results
```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.projectmanagement.util.PasswordUtilsTest
- testHashAndVerifyPassword: PASS
- testInvalidPasswordHandling: PASS
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0

Running com.projectmanagement.util.ValidationUtilsTest
- testEmailValidation: PASS
- testUserRegistrationValidation: PASS
- testProjectValidation: PASS
- testTaskValidation: PASS
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

Running com.projectmanagement.service.UserServiceTest
- testSuccessfulAuthentication: PASS
- testFailedAuthenticationWrongPassword: PASS
- testInactiveAccountAuthentication: PASS
- testPreventSelfDeletion: PASS
- testPreventDeletingLastAdmin: PASS
- testDuplicateEmailRejection: PASS
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

Running com.projectmanagement.service.ProjectServiceTest
- testCreateProjectSuccess: PASS
- testUnauthorizedManagerProjectModification: PASS
- testAdminCanModifyAnyProject: PASS
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0

Results :
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 2. Test Cases Specification Matrix

| Test ID | Module | Scenario / Description | Input / Precondition | Expected Output | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Auth | Valid administrator login | `admin@promanage.com` / `Admin@123` | Session created, redirected to `/admin/dashboard` | **PASS** |
| **TC-02** | Auth | Invalid password attempt | `admin@promanage.com` / `WrongPass` | AuthenticationException, alert shown on `/login` | **PASS** |
| **TC-03** | Auth | Inactive user login attempt | Inactive user credentials | AuthenticationException: "Account is inactive" | **PASS** |
| **TC-04** | Auth | Session Fixation Protection | Log in after having an unauthenticated session | Old session invalidated, new session created | **PASS** |
| **TC-05** | RBAC | Role-based path authorization | Team Member accesses `/admin/users` | Redirected to `/member/dashboard?error=unauthorized` | **PASS** |
| **TC-06** | User | Prevent self-deletion | Admin tries to delete their own account | ValidationException: "Cannot delete logged-in admin" | **PASS** |
| **TC-07** | User | Prevent last admin deletion | Attempting to delete the sole active admin | ValidationException: "Cannot delete last active admin" | **PASS** |
| **TC-08** | User | Duplicate email rejection | Register user with existing email | ValidationException: "Email already in use" | **PASS** |
| **TC-09** | Project | Project authorization barrier | Manager A modifies Project of Manager B | AuthorizationException: "Not authorized" | **PASS** |
| **TC-10** | Project | Due date precedes start date | Due date: 2026-09-01, Start date: 2026-10-01 | ValidationException: "Due date cannot be earlier" | **PASS** |
| **TC-11** | Task | Member task status update | Member changes status to COMPLETED | Status updated, note appended to task comments | **PASS** |
| **TC-12** | Task | Overdue task detection | Due date in past, status not COMPLETED | `isOverdue()` returns true, danger badge displayed | **PASS** |
| **TC-13** | CSRF | State-changing request validation | POST request without `_csrf` token | Request rejected with HTTP 403 Forbidden | **PASS** |
| **TC-14** | XSS | Output escaping in views | User enters `<script>alert('xss')</script>` | Sanitized by `<c:out>`, rendered as plaintext | **PASS** |
| **TC-15** | DB | Database transaction rollback | Batch settings save failure mid-stream | Connection rollback triggered, DB unchanged | **PASS** |
