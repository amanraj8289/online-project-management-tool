# Executive Project Summary & Viva / Defense Preparation

## Project Synopsis
- **Title:** Online Project Management Tool
- **Platform:** Java Web Application (Jakarta Servlet 6.0, JSP, JDBC, MySQL, Bootstrap 5)
- **Target Audience:** College Academic Reviewers, Evaluators, Engineering Faculty, Project Managers, Software Engineers

---

## Common Viva / Defense Questions & Answers

### Q1: Why did you choose Jakarta Servlets & JSP instead of Spring Boot or Node.js?
**Answer:** The goal of this academic project is to demonstrate foundational understanding of enterprise Java web architecture, the HTTP request lifecycle, servlet lifecycle (`init`, `service`, `destroy`), session management, and manual JDBC connection handling. While frameworks like Spring Boot abstract these concepts, implementing them using core Servlets, Filters, and DAOs showcases direct mastery of the Java Enterprise standard.

### Q2: How does the application prevent SQL Injection?
**Answer:** Across all 8 DAO implementations, the application strictly uses `PreparedStatement` with parameterized placeholders (`?`). Values are bound using strongly typed setter methods (`setString`, `setLong`, `setDate`), which causes the database engine to treat inputs purely as data rather than executable SQL syntax.

### Q3: How is password security implemented?
**Answer:** Passwords are never stored in plaintext. We utilize the **BCrypt** strong hashing algorithm with an exponential cost factor of 12. BCrypt automatically generates and incorporates a unique cryptographic salt into each hash, preventing rainbow table attacks and ensuring that identical plaintext passwords yield completely different hash strings.

### Q4: How is Role-Based Access Control (RBAC) enforced?
**Answer:** Access control is enforced at two distinct layers:
1. **Filter Layer (`RoleAuthorizationFilter`):** Intercepts incoming HTTP requests and inspects the user role stored in the verified session before allowing access to URL patterns (`/admin/*`, `/manager/*`, `/member/*`).
2. **Service Layer (`ProjectServiceImpl`, `TaskServiceImpl`):** Performs fine-grained ownership verification (e.g., verifying that a project manager only updates projects where `project.managerId == currentUser.id`).

### Q5: How does the application prevent Cross-Site Request Forgery (CSRF)?
**Answer:** `CsrfTokenUtil` generates a cryptographically random token using `SecureRandom` when a session is initialized. Every state-changing form includes this token as a hidden field (`_csrf`). The `CsrfFilter` intercepts mutating requests (`POST`, `PUT`, `DELETE`) and verifies that the submitted token matches the token stored in the server session.

### Q6: How does the application handle database connections efficiently?
**Answer:** We utilize **HikariCP**, the industry-standard high-performance JDBC connection pool. Instead of incurring the expensive overhead of creating and tearing down socket connections for every request, connections are pre-allocated, verified, and reused from the pool, with connection pooling timeouts and thread-safe double-checked locking in `DatabaseConnection.java`.

### Q7: Where is multithreading demonstrated in the codebase?
**Answer:** In `com.projectmanagement.util.BackgroundJobExecutor`. We configure a controlled `ThreadPoolExecutor` with daemon threads. Audit logging (`ActivityLogServiceImpl`) and system alert dispatching (`NotificationServiceImpl`) are executed asynchronously in background threads, so the user's HTTP request completes instantly without waiting for audit record I/O.
