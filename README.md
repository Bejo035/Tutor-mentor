# TutorMentor

A tutoring and mentoring management platform built with Spring Boot and MongoDB.

## Tech Stack

- **Java 17** + **Spring Boot 3.4.4**
- **MongoDB** (with GridFS for file storage)
- **JWT authentication** (access + refresh tokens via `java-jwt` / `jjwt`)
- **Spring Security** (OAuth2 Resource Server with JWT)
- **springdoc-openapi** (Swagger UI)

## Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB 6+ (replica set required for `@Transactional` support)

## Quick Start

```bash
# Clone and build
mvn clean install

# Run (ensure MongoDB is running)
mvn spring-boot:run
```

The API starts on `http://localhost:8080` by default.

## API Documentation

Once running, open the interactive Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

The raw OpenAPI spec is available at `/v3/api-docs`.

## Project Structure

```
src/main/java/ge/batumi/tutormentor/
  config/           - Application configuration (OpenAPI)
  controller/       - REST controllers (public endpoints)
  controller/admin/ - Admin-only REST controllers
  exceptions/       - Custom exception classes
  handlers/         - Global exception handlers
  manager/          - Business logic orchestrators
  model/db/         - MongoDB document entities
  model/request/    - Incoming request DTOs
  model/response/   - Outgoing response DTOs
  repository/       - Spring Data MongoDB repositories
  security/         - Security config, JWT service, filters
  services/         - Service layer
```

## Authentication Flow

1. **Register** `POST /api/v1/auth/register` - creates account, returns access + refresh tokens
2. **Login** `POST /api/v1/auth/login` - authenticates, returns access + refresh tokens
3. **Access API** - include `Authorization: Bearer <accessToken>` header
4. **Refresh** `POST /api/v1/auth/refresh` - exchange refresh token for new token pair
5. **Logout** `POST /api/v1/auth/logout` - blacklists tokens
