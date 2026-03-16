# Pets API

A Spring Boot REST API for managing pets, built with Java 21.

## Running the Application

```bash
./mvnw spring-boot:run
```

The server starts on **http://localhost:8080**.

## Running Tests

```bash
./mvnw test
```

## API Reference

Base path: `/api/pets`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/pets` | Create a new pet |
| `GET` | `/api/pets` | List all pets |
| `GET` | `/api/pets/{id}` | Get a pet by ID |
| `PUT` | `/api/pets/{id}` | Update a pet |
| `DELETE` | `/api/pets/{id}` | Delete a pet |

### Pet Schema

```json
{
  "name": "string (required)",
  "species": "string (required)",
  "age": "integer ≥ 0 (optional)",
  "ownerName": "string (optional)"
}
```

### Examples

**Create a pet**
```bash
curl -X POST http://localhost:8080/api/pets \
  -H "Content-Type: application/json" \
  -d '{"name":"Buddy","species":"Dog","age":3,"ownerName":"Alice"}'
```

**List all pets**
```bash
curl http://localhost:8080/api/pets
```

**Get a pet by ID**
```bash
curl http://localhost:8080/api/pets/1
```

**Update a pet**
```bash
curl -X PUT http://localhost:8080/api/pets/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Max","species":"Cat","age":5,"ownerName":"Bob"}'
```

**Delete a pet**
```bash
curl -X DELETE http://localhost:8080/api/pets/1
```

### Error Responses

All errors follow [RFC 9457 Problem Detail](https://www.rfc-editor.org/rfc/rfc9457) format:

```json
{
  "type": "about:blank",
  "title": "Pet Not Found",
  "status": 404,
  "detail": "Pet not found with id: 99"
}
```

Validation errors additionally include a field-level `errors` map:

```json
{
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "errors": {
    "name": "Name is required",
    "age": "Age must be 0 or greater"
  }
}
```

## Design Decisions

### Repository abstraction (NoSQL migration readiness)
The persistence layer is hidden behind a `PetRepository` interface. The current implementation (`InMemoryPetRepository`) simulates a relational database using an in-memory map. When the planned migration to a non-relational database takes place, only a new implementation of `PetRepository` needs to be provided — no controller or service code changes are required.

### Plain domain model
`Pet` is a plain Java record with no JPA or persistence annotations. Keeping the domain model free of infrastructure concerns means it can be reused unchanged regardless of which database technology is chosen.

### DTO separation
`PetRequest` and `PetResponse` decouple the API contract from the domain model, allowing each to evolve independently and keeping validation annotations out of the core entity.

### Centralised error handling
`GlobalExceptionHandler` maps all exceptions to RFC 9457 `ProblemDetail` responses, giving API consumers a consistent error format across all failure modes.
