# AGENTS.md

This file gives repository-specific guidance for coding agents working on this project.

## 1) Project Identity

- Name: Tawsila.tn (UniRide university carpooling platform)
- Type: Monolithic Spring Boot backend serving static frontend assets
- Language: Java 17 (backend), HTML/CSS/JavaScript (frontend)
- Build tool: Maven
- Packaging: Spring Boot executable jar

## 2) Product Scope

The platform supports:

- Authentication and user sessions (token flow used by frontend)
- Passenger and driver roles
- Trip creation, listing, and closure
- Reservation lifecycle (create, confirm, cancel)
- Payment status handling
- Notifications
- Admin endpoints
- Health endpoint for monitoring

## 3) Repository Structure

- Backend source: src/main/java/com/covoiturage
- Controllers: src/main/java/com/covoiturage/controller
- Services: src/main/java/com/covoiturage/service
- Domain model: src/main/java/com/covoiturage/model
- Repositories: src/main/java/com/covoiturage/repository
- App config: src/main/resources/application.properties
- Static frontend: src/main/resources/static
- CI workflow: .github/workflows/ci.yml
- Env template: .env.example
- Local helper scripts: start-app.ps1, start-app-postgres.ps1, stop-app.ps1

## 4) Architecture and Layering Rules

Follow this direction strictly:

- Controller -> Service -> Repository -> Persistence
- Controllers handle HTTP mapping and request/response translation only.
- Services contain business rules and state transitions.
- Repositories handle storage mechanics and must not contain business logic.
- Models represent domain entities and enums.

Do not bypass service layer from controllers.

## 5) Persistence Modes

The app supports multiple persistence modes via environment variables.

- memory: default for local testing
- postgres: production-ready mode
- supabase: optional mode if configured

Primary toggle:

- APP_PERSISTENCE_MODE=memory|postgres|supabase

PostgreSQL variables:

- POSTGRES_URL
- POSTGRES_USER
- POSTGRES_PASSWORD
- POSTGRES_SCHEMA (default public)

## 6) Known Important Runtime Behavior

- Server port is controlled by PORT with fallback 8080.
- Health endpoint is available at /api/system/health.
- Frontend API base is relative (/api), so backend and frontend are expected on same host for default behavior.

## 7) Local Development Runbook

Preferred local runs:

- Memory mode: ./start-app.ps1
- Postgres mode using .env values: ./start-app-postgres.ps1

Manual Maven run:

- mvn clean test
- mvn spring-boot:run

If dependency/classpath issues appear in editor, run:

- mvn compile

## 8) Render Deployment Runbook

Use Render as the default production hosting target for this project.

### Render services

- Create one PostgreSQL service.
- Create one Web Service connected to this repository.

### Web Service commands

- Build command: mvn clean package -DskipTests
- Start command: java -jar target/*.jar

### Required environment variables

- APP_PERSISTENCE_MODE=postgres
- POSTGRES_URL=jdbc:postgresql://<host>:<port>/<database>
- POSTGRES_USER=<db-user>
- POSTGRES_PASSWORD=<db-password>
- POSTGRES_SCHEMA=public

### Health check

- Path: /api/system/health

### Verification after deploy

- GET /api/system/health returns UP payload
- Frontend pages load from root URL
- Core flow works: register/login -> search trip -> reserve

## 9) API Surface (High Value)

Authentication:

- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/logout
- GET /api/auth/me
- GET /api/auth/users

Trips:

- POST /api/trajets
- GET /api/trajets
- POST /api/trajets/{id}/close

Reservations:

- POST /api/reservations
- POST /api/reservations/{id}/confirm
- POST /api/reservations/{id}/cancel
- GET /api/reservations
- GET /api/reservations/passager/{passagerId}
- GET /api/reservations/passager/{passagerId}/suivi
- GET /api/reservations/chauffeur/{chauffeurId}/demandes

System:

- GET /api/system/health

## 10) Coding Standards for Agents

- Keep changes minimal and task-focused.
- Preserve existing package structure and naming style.
- Prefer adding/adjusting service methods rather than embedding logic in controllers.
- Keep ASCII text unless file already uses Unicode intentionally.
- Avoid broad refactors unless explicitly requested.
- Do not introduce new frameworks without clear need.

## 11) Error Handling Guidance

- Use existing domain exceptions where appropriate:
  - BusinessException
  - ValidationException
  - NotFoundException
  - InvalidStateException
  - UserBlockedException
- Keep API error behavior compatible with existing ApiExceptionHandler.

## 12) Security and Data Rules

- Never commit real credentials.
- Use environment variables for secrets.
- Respect current password hashing behavior in domain logic.
- Do not log sensitive values (passwords, tokens, raw DB credentials).

## 13) Frontend Integration Rules

- Frontend files are under src/main/resources/static.
- Keep API requests aligned with current base path strategy.
- If deploying frontend and backend on different hosts, add backend CORS config and configurable API base.

## 14) Testing and Validation Expectations

Before finalizing significant backend changes:

- Run mvn clean test when feasible.
- At minimum verify compile success with mvn compile.
- Validate health endpoint and one critical business flow.

## 15) Git and Change Safety

- Do not revert unrelated user changes.
- Avoid destructive git commands.
- Keep commits and patches scoped to the user request.
- If unexpected external modifications appear, pause and ask the user how to proceed.

## 16) Suggested Agent Workflow

1. Read affected controller, service, model, and repository files.
2. Trace the business rule through service layer.
3. Implement smallest safe change.
4. Validate build/tests where possible.
5. Summarize what changed and what remains.

## 17) Maintenance Notes

- Keep this file updated when deployment target, run commands, or architecture decisions change.
- If Render settings change, update Section 8 first.
