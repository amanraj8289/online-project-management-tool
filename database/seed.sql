-- ====================================================================
-- Seed Data for Online Project Management Tool
-- Database Name: project_management_db
-- ====================================================================

USE `project_management_db`;

-- Disable foreign key checks for clean re-seeding if needed
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `notifications`;
TRUNCATE TABLE `activity_logs`;
TRUNCATE TABLE `task_comments`;
TRUNCATE TABLE `tasks`;
TRUNCATE TABLE `project_members`;
TRUNCATE TABLE `projects`;
TRUNCATE TABLE `system_settings`;
TRUNCATE TABLE `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------------------
-- 1. Seed Users
-- Passwords:
-- Admin:        admin@promanage.com    / Admin@123
-- PM 1:         sarah.pm@promanage.com / Manager@123
-- PM 2:         david.pm@promanage.com / Manager@123
-- Dev 1:        alex.dev@promanage.com / Member@123
-- Dev 2:        emily.dev@promanage.com/ Member@123
-- QA 1:         michael.qa@promanage.com/ Member@123
-- Designer:     sophia.ui@promanage.com/ Member@123
-- --------------------------------------------------------------------
INSERT INTO `users` (`id`, `name`, `email`, `password_hash`, `role`, `account_status`, `created_at`) VALUES
(1, 'System Administrator', 'admin@promanage.com', '$2a$12$9jj8ZJTnjQjcYw4JTVzyuOfSR82EmeZ/YQC.EI86yXFrR7422mzmW', 'ADMIN', 'ACTIVE', NOW()),
(2, 'Sarah Jenkins', 'sarah.pm@promanage.com', '$2a$12$2BRjawCnFF62oHIkJWTb1OLCSpAzWOOpxB2V9SbHp.ouIL4VjbX/a', 'PROJECT_MANAGER', 'ACTIVE', NOW()),
(3, 'David Miller', 'david.pm@promanage.com', '$2a$12$2BRjawCnFF62oHIkJWTb1OLCSpAzWOOpxB2V9SbHp.ouIL4VjbX/a', 'PROJECT_MANAGER', 'ACTIVE', NOW()),
(4, 'Alex Rivera', 'alex.dev@promanage.com', '$2a$12$P7XQ3pfV3aOcSpwGkvOD7u43OQr8wZyZplVEhquxywuHgvD4YCY8G', 'TEAM_MEMBER', 'ACTIVE', NOW()),
(5, 'Emily Chen', 'emily.dev@promanage.com', '$2a$12$P7XQ3pfV3aOcSpwGkvOD7u43OQr8wZyZplVEhquxywuHgvD4YCY8G', 'TEAM_MEMBER', 'ACTIVE', NOW()),
(6, 'Michael Scott', 'michael.qa@promanage.com', '$2a$12$P7XQ3pfV3aOcSpwGkvOD7u43OQr8wZyZplVEhquxywuHgvD4YCY8G', 'TEAM_MEMBER', 'ACTIVE', NOW()),
(7, 'Sophia Patel', 'sophia.ui@promanage.com', '$2a$12$P7XQ3pfV3aOcSpwGkvOD7u43OQr8wZyZplVEhquxywuHgvD4YCY8G', 'TEAM_MEMBER', 'ACTIVE', NOW());

-- --------------------------------------------------------------------
-- 2. Seed System Settings
-- --------------------------------------------------------------------
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `updated_at`) VALUES
('app_name', 'ProManage Enterprise', NOW()),
('company_name', 'Enterprise Technologies Ltd.', NOW()),
('default_task_priority', 'MEDIUM', NOW()),
('session_timeout_minutes', '30', NOW()),
('allow_user_registration', 'false', NOW()),
('system_announcement', 'Welcome to ProManage Enterprise Project Management System v1.0. All tasks for sprint 4 are active.', NOW());

-- --------------------------------------------------------------------
-- 3. Seed Projects
-- --------------------------------------------------------------------
INSERT INTO `projects` (`id`, `title`, `description`, `start_date`, `due_date`, `status`, `manager_id`, `created_at`) VALUES
(1, 'Cloud Infrastructure Migration', 'Migrate core monolithic on-premise application services to containerized AWS Kubernetes clusters with high availability and automated disaster recovery.', '2026-09-01', '2026-11-30', 'IN_PROGRESS', 2, NOW()),
(2, 'Mobile Banking Portal Revamp', 'Redesign customer-facing mobile banking interfaces with biometric authentication, instant payments, and accessible design system compliance.', '2026-09-15', '2026-12-15', 'IN_PROGRESS', 3, NOW()),
(3, 'AI-Powered Analytics Dashboard', 'Build predictive customer lifetime value models and integrate real-time pipeline monitoring dashboards with automated anomaly alerts.', '2026-10-01', '2027-01-31', 'PLANNING', 2, NOW()),
(4, 'Legacy Database Modernization', 'Normalize existing legacy MySQL databases, eliminate duplicate indexes, and optimize query latency across high-traffic tables.', '2026-08-01', '2026-09-30', 'COMPLETED', 3, NOW());

-- --------------------------------------------------------------------
-- 4. Seed Project Members
-- --------------------------------------------------------------------
INSERT INTO `project_members` (`project_id`, `user_id`, `joined_at`) VALUES
(1, 4, NOW()), -- Alex on Cloud Migration
(1, 5, NOW()), -- Emily on Cloud Migration
(1, 6, NOW()), -- Michael on Cloud Migration
(2, 4, NOW()), -- Alex on Banking
(2, 6, NOW()), -- Michael on Banking
(2, 7, NOW()), -- Sophia on Banking
(3, 5, NOW()), -- Emily on AI Analytics
(3, 7, NOW()), -- Sophia on AI Analytics
(4, 4, NOW()), -- Alex on DB Modernization
(4, 5, NOW());

-- --------------------------------------------------------------------
-- 5. Seed Tasks
-- --------------------------------------------------------------------
INSERT INTO `tasks` (`id`, `project_id`, `title`, `description`, `assigned_to`, `priority`, `status`, `start_date`, `due_date`, `created_at`) VALUES
-- Tasks for Project 1 (Cloud Migration)
(1, 1, 'Containerize Spring Boot Microservices', 'Write multi-stage Dockerfiles for auth, billing, and notification services with optimized layers.', 4, 'HIGH', 'COMPLETED', '2026-09-05', '2026-09-25', NOW()),
(2, 1, 'Configure Terraform EKS Cluster', 'Provision AWS VPC, subnets, NAT gateways, and Kubernetes worker node groups via Terraform scripts.', 4, 'CRITICAL', 'IN_PROGRESS', '2026-09-26', '2026-10-25', NOW()),
(3, 1, 'Implement CI/CD Deployment Pipelines', 'Create GitHub Actions workflows with automated unit tests, sonar security scanning, and helm deployments.', 5, 'HIGH', 'IN_PROGRESS', '2026-10-01', '2026-10-30', NOW()),
(4, 1, 'Perform Load & Penetration Testing', 'Execute JMeter performance tests simulating 50,000 concurrent sessions and patch security findings.', 6, 'CRITICAL', 'TODO', '2026-11-01', '2026-11-20', NOW()),

-- Tasks for Project 2 (Mobile Banking)
(5, 2, 'Design High-Fidelity Figma Wireframes', 'Deliver responsive mobile wireframes for transfer money, biometric authentication, and transaction history.', 7, 'HIGH', 'COMPLETED', '2026-09-18', '2026-10-05', NOW()),
(6, 2, 'Implement Biometric Fingerprint Login', 'Integrate FaceID and Android BiometricPrompt APIs with secure enclave token storage.', 4, 'CRITICAL', 'IN_PROGRESS', '2026-10-06', '2026-10-28', NOW()),
(7, 2, 'QA Cross-Browser & Device Verification', 'Verify UI rendering and touch gestures on Android 14+ and iOS 18 devices.', 6, 'MEDIUM', 'TODO', '2026-10-20', '2026-11-10', NOW()),

-- Tasks for Project 3 (AI Analytics)
(8, 3, 'Collect & Clean Customer Data Pipelines', 'Aggregate anonymized session logs and build ETL pipelines feeding data warehouse staging tables.', 5, 'MEDIUM', 'TODO', '2026-10-15', '2026-11-15', NOW()),
(9, 3, 'Create UI/UX Design System for Charts', 'Design cohesive color palettes and layout guidelines for Chart.js real-time graph components.', 7, 'LOW', 'TODO', '2026-10-15', '2026-11-05', NOW()),

-- Tasks for Project 4 (DB Modernization - Completed)
(10, 4, 'Audit Missing Foreign Keys & Unused Indexes', 'Run query performance analysis and identify missing composite indexes on audit log tables.', 4, 'MEDIUM', 'COMPLETED', '2026-08-05', '2026-08-20', NOW()),
(11, 4, 'Perform Zero-Downtime Data Migration', 'Execute batch data migration using Percona toolkit with checksum verification.', 5, 'HIGH', 'COMPLETED', '2026-08-21', '2026-09-25', NOW());

-- --------------------------------------------------------------------
-- 6. Seed Task Comments
-- --------------------------------------------------------------------
INSERT INTO `task_comments` (`task_id`, `user_id`, `comment`, `created_at`) VALUES
(1, 4, 'Docker images have been published to Amazon ECR. Size reduced from 650MB to 180MB using Alpine base.', CURRENT_TIMESTAMP),
(2, 4, 'Terraform scripts tested in staging VPC. Working on IAM roles for service accounts (IRSA).', CURRENT_TIMESTAMP),
(2, 2, 'Please ensure security groups restrict direct public access to worker nodes.', CURRENT_TIMESTAMP),
(3, 5, 'GitHub actions pipeline YAML tested successfully with automated staging deployment.', CURRENT_TIMESTAMP),
(5, 7, 'Figma prototypes reviewed and approved by product owner. Handoff assets exported.', CURRENT_TIMESTAMP),
(6, 4, 'Biometric hardware detection implemented. Fallback PIN prompt configured.', CURRENT_TIMESTAMP);

-- --------------------------------------------------------------------
-- 7. Seed Activity Logs
-- --------------------------------------------------------------------
INSERT INTO `activity_logs` (`user_id`, `project_id`, `task_id`, `action_type`, `description`, `created_at`) VALUES
(1, NULL, NULL, 'SYSTEM_STARTUP', 'Administrator initialized system configuration and verified database connectivity.', CURRENT_TIMESTAMP),
(2, 1, NULL, 'PROJECT_CREATED', 'Sarah Jenkins created project "Cloud Infrastructure Migration".', CURRENT_TIMESTAMP),
(2, 1, 1, 'TASK_CREATED', 'Task "Containerize Spring Boot Microservices" assigned to Alex Rivera.', CURRENT_TIMESTAMP),
(4, 1, 1, 'TASK_STATUS_UPDATED', 'Alex Rivera changed task status to COMPLETED.', CURRENT_TIMESTAMP),
(3, 2, NULL, 'PROJECT_CREATED', 'David Miller created project "Mobile Banking Portal Revamp".', CURRENT_TIMESTAMP),
(3, 2, 6, 'TASK_ASSIGNED', 'Task "Implement Biometric Fingerprint Login" assigned to Alex Rivera.', CURRENT_TIMESTAMP),
(4, 2, 6, 'TASK_STATUS_UPDATED', 'Alex Rivera changed task status to IN_PROGRESS.', CURRENT_TIMESTAMP);

-- --------------------------------------------------------------------
-- 8. Seed Notifications
-- --------------------------------------------------------------------
INSERT INTO `notifications` (`user_id`, `message`, `notification_type`, `is_read`, `created_at`) VALUES
(4, 'You have been assigned to task: "Configure Terraform EKS Cluster"', 'TASK_ASSIGNED', FALSE, CURRENT_TIMESTAMP),
(4, 'You have been assigned to task: "Implement Biometric Fingerprint Login"', 'TASK_ASSIGNED', TRUE, CURRENT_TIMESTAMP),
(5, 'You have been assigned to task: "Implement CI/CD Deployment Pipelines"', 'TASK_ASSIGNED', FALSE, CURRENT_TIMESTAMP),
(6, 'New task pending QA: "Perform Load & Penetration Testing"', 'TASK_ASSIGNED', FALSE, CURRENT_TIMESTAMP),
(7, 'Figma wireframes for Mobile Banking Portal Revamp approved.', 'PROJECT_UPDATE', TRUE, CURRENT_TIMESTAMP);
