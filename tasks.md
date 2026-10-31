# RoutEd Development Execution Plan (`tasks.md`)

This document contains the step-by-step execution roadmap for building **RoutEd**—an AI-powered B2B SaaS platform for dynamic course personalization and learner analytics. 

> **Instructions for the Coding Agent:**
> 1. Execute tasks sequentially from Phase 1 to Phase 10.
> 2. After completing each sub-task, update the checkbox from `[ ]` to `[x]`.
> 3. Do not jump between phases without completing prerequisites.
> 4. Ensure all code follows clean architecture, proper error handling, and type safety/DTO validations.

---

## Technical Stack & Infrastructure Summary

- **Backend Framework**: Spring Boot 3.x (Java 17) + Maven
- **Security**: Spring Security + Stateless JWT Token Authentication
- **Database**: PostgreSQL 16 (Docker Container) + Spring Data JPA / Hibernate
- **AI Integration**: Local Ollama (`llama3` model running via Docker Container)
- **Frontend Framework**: React 18+ (Vite) + TailwindCSS + Shadcn UI + Lucide Icons + Framer Motion + Recharts
- **Background Jobs & Email**: Spring `@Scheduled` Cron + JavaMailSender (SMTP)

---

## Phase 1: Environment Setup & Docker Infrastructure

- [x] **Task 1.1: Docker Compose Environment**
  - Create `docker-compose.yml` containing PostgreSQL 16 (`routed_db`, `routed_user`, `routed_password`) on port `5432` and Ollama on port `11434`.
  - Add volume persistence for both Postgres and Ollama data.
  - Verify containers run using `docker compose up -d` and pull `llama3` (`docker exec -it routed_ollama ollama run llama3`).

- [x] **Task 1.2: Spring Boot Project Initialization**
  - Initialize Spring Boot project with dependencies: `Spring Web`, `Spring Data JPA`, `Spring Security`, `PostgreSQL Driver`, `Lombok`, `JavaMailSender`, `Validation`.
  - Configure `src/main/resources/application.yml` with database credentials, JPA settings (`hibernate.ddl-auto: update`), JWT secret, and Ollama REST base URL (`http://localhost:11434`).

- [x] **Task 1.3: React + Vite Frontend Setup**
  - Initialize React Vite application (`frontend/`).
  - Configure TailwindCSS, Shadcn UI components (Button, Card, Input, Dialog, Progress, Badge, Toast, Table, Select), `lucide-react`, `framer-motion`, `recharts`, and `axios`.
  - Set up API base URL configuration and Axios interceptors for JWT token injection.

---

## Phase 2: Core Database Schema & Spring Data JPA Entities

- [x] **Task 2.1: User & Authentication Entities**
  - Create `User` entity with fields: `id` (UUID), `name`, `email` (unique), `passwordHash`, `role` (`ADMIN`, `LEARNER`), `createdAt`.
  - Create `UserRepository` with query methods `findByEmail`.

- [x] **Task 2.2: Course & Content Hierarchy Entities**
  - Create `Course` entity: `id` (UUID), `title`, `description`, `admin` (`@ManyToOne User`), `createdAt`.
  - Create `Module` entity: `id` (UUID), `course` (`@ManyToOne Course`), `title`, `description`, `defaultOrderIndex` (Integer), `contentUrl`, `durationMinutes`, `skillTag` (String, e.g., "ARRAYS").

- [x] **Task 2.3: Assessment & Onboarding Entities**
  - Create `Assessment` entity: `id`, `course` (`@ManyToOne`), `title`.
  - Create `Question` entity: `id`, `assessment` (`@ManyToOne`), `questionText`, `optionsJson`, `correctOption`, `skillTag`.
  - Create `LearnerAssessment` entity: `id`, `learner`, `assessment`, `overallScore`, `skillScoresJson` (`jsonb` map of skill tags to score percentages), `completedAt`.

- [x] **Task 2.4: Personalization, Tracking & Logging Entities**
  - Create `LearnerPath` entity: `id`, `learner`, `course`, `moduleOrderJson` (`jsonb` list of Module UUIDs), `skippedModuleIds` (`jsonb` list of Module UUIDs), `aiRationaleJson` (`jsonb` map of reasons), `generatedAt`.
  - Create `ProgressEvent` entity: `id`, `learner`, `module`, `completedAt` with unique composite constraint `(learner_id, module_id)`.
  - Create `EmailLog` entity: `id`, `learner`, `emailType` (`WELCOME`, `WEEKLY_SUMMARY`), `status` (`QUEUED`, `SENT`, `FAILED`), `sentAt`.

---

## Phase 3: Authentication & Security Subsystem

- [x] **Task 3.1: JWT Security Configuration**
  - Implement `JwtProvider` utility for generating, validating, and extracting claims from JWTs.
  - Implement `JwtAuthFilter` extending `OncePerRequestFilter`.
  - Configure Spring Security `SecurityFilterChain` with CORS support, stateless session management, and route protections (`/api/auth/**` public, `/api/admin/**` restricted to `ADMIN`, `/api/learner/**` restricted to `LEARNER`).

- [x] **Task 3.2: Auth REST Controller & DTOs**
  - Create `AuthService` and `AuthController` with endpoints:
    - `POST /api/auth/register` (Register learner or admin)
    - `POST /api/auth/login` (Returns JWT access token, user info, and role)
    - `GET /api/auth/me` (Fetch authenticated user profile)

- [x] **Task 3.3: Frontend Auth State & Guard Components**
  - Implement AuthContext / Zustand store for token management and user state.
  - Create Protected Route wrappers (`<AdminRoute />`, `<LearnerRoute />`) in React Router.
  - Build sleek, modern Login and Register pages using Shadcn UI forms.

---

## Phase 4: Admin Course & Assessment Management Subsystem

- [x] **Task 4.1: Admin Course & Module APIs**
  - Create `CourseController` and `CourseService`:
    - `POST /api/admin/courses` (Create course)
    - `GET /api/admin/courses` (List admin's courses)
    - `POST /api/admin/courses/{id}/modules` (Add/order modules)
    - `PUT /api/admin/modules/{id}` & `DELETE /api/admin/modules/{id}`

- [x] **Task 4.2: Admin Assessment & Question Management APIs**
  - Create `AssessmentController`:
    - `POST /api/admin/courses/{id}/assessments` (Create diagnostic test)
    - `POST /api/admin/assessments/{id}/questions` (Add questions tagged with skill domains)

- [x] **Task 4.3: Admin Course Builder Frontend**
  - Build multi-step Admin Dashboard page:
    - Step 1: Course meta details.
    - Step 2: Module list builder (drag-and-drop or order index inputs).
    - Step 3: Diagnostic Assessment question generator with skill tag assignment.

---

## Phase 5: Learner Onboarding & Diagnostic Engine

- [x] **Task 5.1: Learner Assessment Submission API**
  - Create `AssessmentService`:
    - `GET /api/learner/courses/{id}/assessment` (Fetch onboarding test)
    - `POST /api/learner/assessments/{id}/submit` (Evaluate submitted options, calculate overall score & domain-wise mastery percentage e.g. `{"ARRAYS": 80, "TREES": 20}`).

- [x] **Task 5.2: Diagnostic Assessment UI**
  - Build interactive learner onboarding quiz wizard with progress indicator and instant result summary.

---

## Phase 6: Local Ollama AI Personalization Engine

- [x] **Task 6.1: Ollama Client Integration Service**
  - Create `OllamaLlmService` using Spring `RestClient` / `WebClient` pointing to `http://localhost:11434/api/generate`.
  - Construct prompt containing:
    - Full list of modules (IDs, titles, skillTags, defaultOrderIndex).
    - Learner domain mastery scores.
  - Instruct LLM to output pure JSON matching structure:
    ```json
    {
      "moduleOrder": ["uuid-1", "uuid-3", "uuid-2"],
      "skippedModuleIds": ["uuid-4"],
      "aiRationale": {
        "uuid-1": "Placed first due to 20% score in Trees.",
        "uuid-4": "Skipped because student achieved 90% in Arrays."
      }
    }
    ```

- [x] **Task 6.2: AI Path Personalization Orchestrator**
  - Create `PathPersonalizationService`:
    - Triggered automatically after diagnostic submission.
    - Handles JSON parsing, fallback to default course module sequence in case of LLM parse failure/timeout.
    - Persists generated path to `LearnerPaths` table.
    - Exposes `GET /api/learner/courses/{id}/path`.

---

## Phase 7: Learner Experience Dashboard & Progress Event Engine

- [x] **Task 7.1: Progress Tracking APIs**
  - Create `ProgressService` & `ProgressController`:
    - `POST /api/learner/modules/{id}/complete` (Logs idempotent completion timestamp in `ProgressEvents`).
    - `GET /api/learner/courses/{id}/progress` (Returns percentage completion, completed module IDs, current active module).

- [x] **Task 7.2: Learner Vibe Dashboard UI**
  - Build aesthetic Learner Course View:
    - Dynamic course roadmap with Framer Motion animated cards.
    - Visual indicators for completed, active, and AI-skipped modules.
    - Module detail view modal with "Mark Complete" button updating progress bar in real time.
    - AI Path Reasoning banner explaining why the route was tailored for them.

---

## Phase 8: AI Email Pipeline & Background Scheduler

- [x] **Task 8.1: Email Sending Service & Templates**
  - Configure `EmailService` using `JavaMailSender`.
  - Create HTML template for Day 1 AI Welcome Email and Weekly Summary Email.

- [x] **Task 8.2: Async Welcome Email Trigger**
  - Upon path generation completion, asynchronously trigger `EmailService.sendWelcomeEmail(learner, pathRationale)` and record log in `EmailLogs`.

- [x] **Task 8.3: Weekly Progress Cron Scheduler**
  - Implement `WeeklyReportScheduler` with `@Scheduled(cron = "0 0 9 * * MON")`.
  - Fetch active learners, invoke Ollama to draft a 2-sentence encouraging progress summary, send SMTP emails, and update `EmailLogs` (`QUEUED` -> `SENT` / `FAILED`).

---

## Phase 9: Admin Analytics & Learner Health Score Subsystem

- [x] **Task 9.1: Learner Health Score Calculator**
  - Implement Health Score algorithm in `AnalyticsService`:
    $$\text{Health Score} = (\text{Completion Pace} \times 0.5) + (\text{Login Recency} \times 0.3) + (\text{Avg Assessment Score} \times 0.2)$$
  - Classify scores: `Good` (>= 75), `At-Risk` (50-74), `Critical` (< 50).

- [x] **Task 9.2: Admin Analytics APIs**
  - Create `AnalyticsController`:
    - `GET /api/admin/courses/{id}/analytics` (Course completion %, total enrolled, drop-off module heatmap).
    - `GET /api/admin/courses/{id}/health-scores` (List of learners with health scores and risk status).

- [x] **Task 9.3: Glassmorphism Admin Dashboard UI**
  - Build Admin Analytics View with Recharts:
    - Enrollment and completion statistics cards.
    - Drop-off heatmap bar chart (pinpointing which module learners abandon).
    - At-risk learners table with status badges and intervention alerts.

---

## Phase 10: Integration Testing & Final Polish

- [x] **Task 10.1: End-to-End Workflow Verification**
  - Admin logs in -> Creates Course -> Adds Modules -> Adds Diagnostic Quiz.
  - Learner registers -> Takes Assessment -> Local Ollama generates Custom Path -> Day 1 Email logs created.
  - Learner completes modules -> Progress updates -> Admin Dashboard reflects Health Score and Analytics.

- [x] **Task 10.2: UI Vibe & Polish**
  - Add dark/light mode toggle with TailwindCSS.
  - Ensure all loading states use Shadcn Skeleton loaders.
  - Add toast notifications for key user actions (module completed, course created, quiz submitted).
