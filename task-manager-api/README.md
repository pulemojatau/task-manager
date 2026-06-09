# Task Manager API

A Spring Boot REST API for task management with JWT authentication.

## Tech Stack

- Java 17
- Spring Boot 3.4.6
- Spring Security + JWT
- PostgreSQL
- Lombok
- Springdoc OpenAPI (Swagger)

## Running the Application

```bash
./mvnw spring-boot:run
```

The server starts on port **8085**.

## Useful Links

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8085/swagger-ui/index.html |
| API Docs (JSON) | http://localhost:8085/v3/api-docs |

## API Endpoints

### Auth (public)
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and get JWT token

### Tasks (requires JWT)
- `GET /api/tasks` — Get all tasks for authenticated user
- `POST /api/tasks` — Create a task
- `PUT /api/tasks/{id}` — Update a task
- `DELETE /api/tasks/{id}` — Delete a task

### Health
- `GET /api/health` — Health check
