# Academic Review & Marking Rubric Compliance Guide

This document maps all core academic requirements and Java programming concepts directly to their concrete implementations in the source code.

---

## 1. Core Java & Object-Oriented Programming (OOP)

### A. Encapsulation
- **Where in Code:** All Model classes in `com.projectmanagement.model`:
  - `User.java`: Private attributes (`name`, `email`, `passwordHash`, `role`, `accountStatus`), public constructors, getters, setters, and business helper methods (`isAdmin()`, `isProjectManager()`).
  - `Project.java`: Encapsulated dates, status, dynamic progress calculations (`getProgressPercentage()`), and overdue logic (`isOverdue()`).
  - `Task.java`: Encapsulated attributes, state validation, and joined view fields.
- **Why:** Protects internal object representation from unauthorized external mutations and provides a clean API for views and controllers.

### B. Inheritance
- **Where in Code:**
  - `BaseEntity.java` (`src/main/java/com/projectmanagement/model/BaseEntity.java`): Abstract base class encapsulating primary key `id`, audit timestamps `createdAt` and `updatedAt`, and uniform `equals()` / `hashCode()` contracts.
  - Subclasses: `User`, `Project`, `ProjectMember`, `Task`, `TaskComment`, `ActivityLog`, `SystemSetting`, `Notification` all extend `BaseEntity`.
  - Exception hierarchy: `AppException` extends `RuntimeException`, and specific domain exceptions (`ResourceNotFoundException`, `ValidationException`, `AuthorizationException`, `AuthenticationException`, `DatabaseException`) inherit from `AppException`.

### C. Polymorphism (Runtime & Dynamic Dispatch)
- **Where in Code:**
  - Dynamic Interface Implementation: `UserService` interface instantiated via `UserServiceImpl`, `ProjectService` via `ProjectServiceImpl`, `TaskService` via `TaskServiceImpl`.
  - Service Factory: `ServiceFactory.java` provides polymorphic access to interface abstractions, allowing alternative mock or stub implementations during testing (`UserServiceTest.java`, `ProjectServiceTest.java`).
  - Overridden methods: `toString()`, `equals()`, and `hashCode()` implementations across all model entities.

### D. Interfaces
- **Where in Code:**
  - **DAO Layer:** `UserDAO`, `ProjectDAO`, `ProjectMemberDAO`, `TaskDAO`, `TaskCommentDAO`, `ActivityLogDAO`, `SystemSettingsDAO`, `NotificationDAO`.
  - **Service Layer:** `UserService`, `ProjectService`, `TaskService`, `DashboardService`, `ActivityLogService`, `NotificationService`, `SystemSettingsService`.

### E. Exception Handling
- **Where in Code:** `com.projectmanagement.exception` package:
  - `AppException.java`: Base runtime exception.
  - `ValidationException.java`: Collects multi-field validation error messages.
  - `AuthorizationException.java`: Raised when a user attempts actions outside their role or project scope.
  - `AuthenticationException.java`: Raised for invalid credentials or inactive accounts.
  - `ResourceNotFoundException.java`: Raised when queries by ID yield no records.
  - `DatabaseException.java`: Catches checked `SQLException`, logs the internal database trace securely, and shields internal schema details from the user.

### F. Collections & Generics
- **Where in Code:**
  - Strongly-typed collections: `List<User>`, `List<Project>`, `List<Task>`, `List<ProjectProgressDTO>`, `List<ActivityLog>`.
  - Generic Maps: `Map<String, Integer>`, `Map<String, String>` used for Chart.js status distributions and system settings.
  - Java Stream API: Filtering, mapping, and counting across collection streams (`userDatabase.values().stream().filter(...)`).

### G. Multithreading & Synchronization
- **Where in Code:**
  - `BackgroundJobExecutor.java` (`com.projectmanagement.util`): Controlled `ThreadPoolExecutor` with worker threads managing asynchronous audit logging (`ActivityLogServiceImpl`) and system alert dispatch (`NotificationServiceImpl`).
  - `DatabaseConnection.java`: Thread-safe double-checked locking idiom with `synchronized(lock)` for connection pool initialization and shutdown hooks.

---

## 2. Database Integration & JDBC

### A. Connection Pooling & Security
- **Implementation:** `DatabaseConnection.java` utilizes **HikariCP** for high-throughput connection pooling.
- **Zero Hardcoded Credentials:** `AppConfig.java` reads credentials from environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`) with fallback to external `db.properties`.

### B. PreparedStatement & SQL Injection Prevention
- **Implementation:** Every single query across all 8 DAO implementations (`UserDAOImpl`, `ProjectDAOImpl`, `TaskDAOImpl`, etc.) strictly uses parameterized `PreparedStatement` with index binding. No dynamic SQL string concatenation is used.

### C. Resource Management
- **Implementation:** Every connection, statement, and result set is managed using the Java **try-with-resources** statement, guaranteeing prompt return of connections to the pool even during exceptions.

### D. Database Transactions
- **Implementation:** `SystemSettingsDAOImpl.saveSettings()` and `ProjectServiceImpl.updateProject()` demonstrate explicit transaction management (`conn.setAutoCommit(false)`, `batch.executeBatch()`, `conn.commit()`, with rollback handling in `catch` blocks).

---

## 3. Servlets and Web MVC Integration

| MVC Component | Technologies Used | Implementation Classes |
| :--- | :--- | :--- |
| **Model** | Java POJOs, DTOs | `User`, `Project`, `Task`, `TaskComment`, `DashboardMetricsDTO`, `ProjectProgressDTO` |
| **View** | JSP, JSTL 3.0, Bootstrap 5, Chart.js | `WEB-INF/views/admin/*`, `manager/*`, `member/*`, `auth/*`, `common/*` |
| **Controller** | Jakarta Servlets (Tomcat 10.1) | `AuthController`, `AdminDashboardController`, `ProjectController`, `TaskController`, etc. |
| **Middleware** | Jakarta Filters | `AuthenticationFilter`, `RoleAuthorizationFilter`, `CsrfFilter`, `CharacterEncodingFilter` |

---

## 4. Security & Robustness Highlights

1. **Password Hashing:** Industry-standard **BCrypt** with salt and cost factor 12 (`PasswordUtils.java`).
2. **Session Security:**
   - Session fixation protection (session invalidated and recreated on login).
   - 30-minute idle session timeout.
   - HttpOnly and SameSite cookie flags configured in `web.xml`.
3. **Cross-Site Request Forgery (CSRF):** Cryptographic session tokens generated via `SecureRandom` and enforced by `CsrfFilter` on all mutating operations.
4. **Cross-Site Scripting (XSS):** All dynamic user content in JSP views is escaped using JSTL `<c:out value="..." />`.
5. **Role-Based Access Control (RBAC):** Server-side verification on both filter and service layers preventing URL manipulation or parameter tampering.
