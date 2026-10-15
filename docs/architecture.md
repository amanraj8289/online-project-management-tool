# System Architecture - Online Project Management Tool

## 1. Architectural Style: Layered MVC (Model-View-Controller)

The application follows the classic Java Enterprise MVC architectural pattern adhering to Separation of Concerns (SoC) and clean layered boundaries:

```
+-------------------------------------------------------------------------+
|                              Presentation Layer                         |
|  - JSP Views (JSTL 3.0, Bootstrap 5, FontAwesome/BI, Chart.js 4.4)      |
|  - HTML5 / CSS3 / JavaScript (Client-side validation & dynamic graphs)   |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                             Security & Filter Layer                     |
|  - CharacterEncodingFilter (UTF-8 enforcement)                          |
|  - AuthenticationFilter (Session validation & login redirection)        |
|  - RoleAuthorizationFilter (RBAC checks: Admin, PM, Member)             |
|  - CsrfFilter (Anti-CSRF token verification on state mutations)         |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                             Controller Layer (Servlets)                 |
|  - AuthController, AdminDashboardController, ManagerDashboardController |
|  - MemberDashboardController, ProjectController, TaskController         |
|  - UserController, ProfileController, SettingsController, ApiChartCtrl  |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                               Service Layer                             |
|  - UserService, ProjectService, TaskService                             |
|  - DashboardService, ActivityLogService, NotificationService            |
|  - SystemSettingsService                                                |
|  - BackgroundJobExecutor (Controlled ThreadPool for async tasks)        |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                             Data Access Layer (DAO)                     |
|  - UserDAO, ProjectDAO, ProjectMemberDAO, TaskDAO                       |
|  - TaskCommentDAO, ActivityLogDAO, SystemSettingsDAO, NotificationDAO   |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                         Connection Pooling & Database                   |
|  - HikariCP Connection Pool (Thread-safe, max lifetime, leak detection)  |
|  - MySQL 8.0+ Database (project_management_db)                          |
+-------------------------------------------------------------------------+
```

## 2. Request Lifecycle & Post/Redirect/Get Pattern

1. **Client Request**: Browser sends an HTTP request with session cookies and optional CSRF tokens.
2. **Filter Chain Processing**:
   - `CharacterEncodingFilter` ensures UTF-8 encoding across request and response streams.
   - `AuthenticationFilter` validates active session `currentUser`. Unauthenticated requests to protected endpoints redirect to `/login`.
   - `RoleAuthorizationFilter` checks user role against required permission path prefixes (`/admin/*`, `/manager/*`, `/member/*`).
   - `CsrfFilter` inspects `POST/PUT/DELETE` requests to ensure valid matching anti-CSRF token.
3. **Controller Execution**: Dedicated Jakarta Servlet receives the request, extracts and sanitizes parameters via `ValidationUtils`, and invokes the respective Service interface.
4. **Service Execution**: Enforces business logic (e.g., verifying that a project manager only accesses their own projects, ensuring passwords meet complexity and hashing via `PasswordUtils`, calculating progress).
5. **DAO & JDBC**: Prepared statements are executed using connections borrowed from HikariCP. Database results are mapped into strongly-typed Model POJOs.
6. **Post/Redirect/Get**: State-changing POST submissions commit the change and issue an HTTP 302 Redirect to a GET endpoint to prevent duplicate form submissions upon page reload.
7. **View Rendering**: JSP views consume request attributes using JSTL `<c:out>` to prevent Cross-Site Scripting (XSS).

## 3. Asynchronous Concurrency Model

Audit logging (`activity_logs`) and system alert dispatch (`notifications`) are offloaded to `BackgroundJobExecutor` using a managed, daemon-threaded `ThreadPoolExecutor`. This guarantees zero latency overhead on the user's synchronous web request.
