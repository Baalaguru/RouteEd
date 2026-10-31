# RouteEd

RouteEd is a multi-tenant B2B SaaS platform that generates personalized learning paths from learner assessments and gives training organizations actionable engagement analytics.

## Repository layout

```text
RouteEd/
  backend/       Spring Boot REST API, PostgreSQL migrations, JWT security
  frontend/      React + TypeScript + Tailwind learner/admin application
  REQUIREMENTS.md Product requirements and architecture blueprint
```

## Local development

### 1. Start the complete Docker stack

```bash
docker compose up -d api
```

### 2. Run the web application

```bash
cd frontend
npm install
npm run dev
```

Install Apache Maven 3.9+ if `mvn` is not available. You can then generate a Maven Wrapper for teammates:

```bash
mvn wrapper:wrapper
```

The containerized API, PostgreSQL, and local Ollama are then available automatically. Open `http://localhost:5173`.

### Optional: run API outside Docker

```bash
cd backend
mvn spring-boot:run
```

For the complete user walkthrough, read [END_TO_END_GUIDE.md](END_TO_END_GUIDE.md).

## Environment

Copy `backend/.env.example` to `backend/.env` and replace values for any real deployment. Never commit production secrets.

## Current foundation

- Spring Boot API layout for authentication, courses, assessments, enrollment, personalization, progress, email, and analytics.
- PostgreSQL schema through Flyway.
- Multi-tenant course CRUD endpoints with organization scoping.
- React dashboard shell, learner path view, admin course view, and API client.
- Docker Compose PostgreSQL environment.

Read [REQUIREMENTS.md](REQUIREMENTS.md) before extending the product.
