# Spring Boot Supermarket API 🛒

RESTful API for a supermarket management system. Completed as a technical assessment in under 3 hours using **Java 17** & **Spring Boot 3**.

> **Original Challenge:** Based on the technical test proposed by [TodoCode](https://www.youtube.com/watch?v=l-Bl45I6UEY&t=9870s).

## 🚀 Key Differences & Improvements
Unlike the original implementation, this project focuses on industry best practices and clean architecture:

- **Dependency Injection by Constructor:** Replaced field injection (`@Autowired` on fields) with constructor-based injection to ensure immutability and easier unit testing.
- **Robust Error Handling:** Implemented a `GlobalExceptionHandler` (`@ControllerAdvice`) to catch exceptions centrally.
- **Standardized API Responses:** All errors return a consistent `ErrorResponse` JSON structure (timestamp, status, error, message), avoiding raw stack traces to the client.
- **Dynamic API Documentation (OpenAPI/Swagger):** The project features Dynamic API Documentation using SpringDoc OpenAPI. This automatically generates the OpenAPI 3.x Definition File (/v3/api-docs) and an interactive Swagger UI (/swagger-ui.html). This significantly improves Developer Experience (DX) by providing a real-time, self-updating contract of the API schemas and responses.

## 🛠 Tech Stack & Patterns

The project utilizes modern Java features and Spring Boot patterns:

* **Java 17:** Leveraging records (optional) and Functional Programming features.
* **Spring Boot 3.5.7:** Latest framework version.
* **JPA / Hibernate:** For ORM and database interactions.
* **Lombok:** Used to reduce boilerplate code via `@Data`, `@RequiredArgsConstructor`, and especially the **Builder Pattern** (`@Builder`) for cleaner object creation.
* **DTO Pattern:** Separation between Persistence Entities and Data Transfer Objects to protect the database schema.
* **Functional Programming:** Utilization of Java Streams API for filtering and mapping collections efficiently.

## ⚙️ Configuration

The project uses **PostgreSQL** (configured in `application.properties`). Ensure you have a database instance running or update the configuration to use H2 for testing.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/supermarket_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```
---
## 🙏 Acknowledgments and Credits

This project is based on the **Java Spring Boot Technical Test** solved step-by-step by **TodoCode Academy** in their YouTube video. I am grateful for their initial guide and inspiration.

* **Content Creator:** [TodoCode Academy](https://www.youtube.com/@todocodeacademy)
* **Original Video:** [PRUEBA TÉCNICA JAVA SPRING BOOT](https://www.youtube.com/watch?v=l-Bl45I6UEY&t=9870s)

---



