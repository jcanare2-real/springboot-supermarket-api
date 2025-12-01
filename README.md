# Supermarket Management API (Java 17 & Spring Boot 3 + JWT)

RESTful API for a supermarket management system. Originally completed as a technical assessment in under 3 hours using **Java 17** & **Spring Boot 3**, now evolved into a production-ready architecture with **Security** and **Testing**.

> **Original Challenge:** Based on the technical test proposed by [TodoCode](https://www.youtube.com/watch?v=l-Bl45I6UEY&t=9870s).

## 🚀 Key Differences & Improvements
Unlike the original implementation, this project focuses on industry best practices, clean architecture, and **Security**:

- **🛡️ Robust Security with JWT and RBAC:** Full implementation of **Spring Security 6** with **Stateless JWT (JSON Web Tokens)** authentication. It includes Role-Based Access Control (RBAC) to protect sensitive endpoints.
- **Dependency Injection by Constructor:** Replaced field injection (`@Autowired` on fields) with constructor-based injection to ensure immutability and easier unit testing.
- **Robust Error Handling:** Implemented a `GlobalExceptionHandler` (`@ControllerAdvice`) to catch exceptions centrally.
- **Standardized API Responses:** All errors return a consistent `ErrorResponse` JSON structure (timestamp, status, error, message), avoiding raw stack traces.
- **Dynamic API Documentation:** Integrated with **OpenAPI/Swagger**. The UI now supports JWT authorization, allowing you to test secured endpoints directly from the browser.

## 🔐 Security & Authentication
The API is secured using **JWT (JSON Web Tokens)**.

### Roles (RBAC)
*   **ADMIN:** Can manage inventory (Create/Delete Products), edit sales, and view all data.
*   **SELLER:** Can register sales and view history.
*   **USER:** Read-only access to products.

### Authentication Flow
1.  **Register:** `POST /auth/register` (Creates a user and returns a token).
2.  **Login:** `POST /auth/login` (Returns a JWT Token).
3.  **Access:** Send the token in the header: `Authorization: Bearer <YOUR_TOKEN>` for protected routes.

## 🛠 Tech Stack & Patterns

The project utilizes modern Java features and Spring Boot patterns:

* **Java 17:** Leveraging records and Functional Programming features.
* **Spring Boot 3.5.7:** Latest framework version.
* **Spring Security 6:** For Authentication and Authorization.
* **JJWT:** Library for JWT generation and validation.
* **JPA / Hibernate:** For ORM and database interactions.
* **Lombok:** Used to reduce boilerplate (`@Data`, `@Builder`, `@RequiredArgsConstructor`).
* **DTO Pattern:** Separation between Persistence Entities and Data Transfer Objects.
* **Testing:** JUnit 5, Mockito, and `Spring Security Test` (`@WithMockUser`).

## ⚙️ Configuration

The project uses **PostgreSQL**. Update `src/main/resources/application.properties` with your credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/supermarket_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# Security (In a real scenario, use env variables)
# jwt.secret=YOUR_SUPER_SECRET_KEY
```

## 🧪 Testing Strategy
The project includes a comprehensive testing suite covering different layers:
*   **Unit Tests:** Service layer logic isolated with Mockito.
*   **Integration Tests (Slice):** Controller layer tested with `@WebMvcTest` and `@WithMockUser` to verify HTTP status codes, JSON serialization, and Security rules.

## 📚 API Documentation
Once the application is running, access the interactive documentation:
*   **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
    *   *Note: Use the "Authorize" button in Swagger to input your Bearer Token.*

---
## 🙏 Acknowledgments and Credits

This project is based on the **Java Spring Boot Technical Test** solved step-by-step by **TodoCode Academy** in their YouTube video. I am grateful for their initial guide and inspiration.

* **Content Creator:** [TodoCode Academy](https://www.youtube.com/@todocodeacademy)
* **Original Video:** [PRUEBA TÉCNICA JAVA SPRING BOOT](https://www.youtube.com/watch?v=l-Bl45I6UEY&t=9870s)

---

