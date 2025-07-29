# Hello API - Database-Powered Spring Boot Application 🚀

## 🎯 About This Project
This is my **database-powered REST API** built with Spring Boot as part of my **6-month backend developer journey**.

**Major Achievement:** Successfully evolved from Day 29 (simple Hello World API) to Day 30 (complete CRUD database application)!

## 📊 Project Status
- **Day:** 30 of 180 (6-month roadmap)
- **Month 1:** ✅ COMPLETE (56/56 algorithm problems solved)
- **Month 2:** 🔥 IN PROGRESS (Building real backend applications)
- **Status:** **FULL DATABASE CRUD API WORKING!** 🎉

## 🏆 Major Milestones
- **Day 29:** First Spring Boot API with 4 endpoints
- **Day 30:** Complete database integration with user management
- **Next:** Advanced features, authentication, relationships

## 🔧 Technologies Used
- **Java 20.0.1** - Programming language
- **Spring Boot 3.5.4** - Web framework
- **Spring Data JPA** - Database ORM layer
- **H2 Database** - Embedded SQL database
- **Hibernate** - JPA implementation
- **Maven** - Build and dependency management
- **Jakarta Validation** - Input validation

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE USERS (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    age INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## 🌐 API Endpoints

### 🔥 User Management (CRUD Operations)

#### 1. Create New User
- **URL:** `POST /api/users`
- **Content-Type:** `application/json`
- **Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "age": 30
}
```
- **Success Response:** `201 Created`
```json
{
  "success": true,
  "message": "User created successfully!",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "age": 30,
    "createdAt": "2025-07-29T...",
    "updatedAt": "2025-07-29T...",
    "fullName": "John Doe"
  },
  "id": 1
}
```

#### 2. Get All Users
- **URL:** `GET /api/users`
- **Query Parameters (Optional):**
    - `firstName` - Filter by first name
    - `minAge` - Minimum age filter
    - `maxAge` - Maximum age filter
- **Examples:**
    - `GET /api/users` - All users
    - `GET /api/users?firstName=John` - Users named John
    - `GET /api/users?minAge=18&maxAge=65` - Users aged 18-65
- **Response:** `200 OK` + Array of users

#### 3. Get User by ID
- **URL:** `GET /api/users/{id}`
- **Example:** `GET /api/users/1`
- **Success Response:** `200 OK` + User object
- **Error Response:** `404 Not Found` if user doesn't exist

#### 4. Update Existing User
- **URL:** `PUT /api/users/{id}`
- **Content-Type:** `application/json`
- **Request Body:** Same as create user
- **Success Response:** `200 OK` + Updated user object
- **Error Response:** `404 Not Found` or `400 Bad Request`

#### 5. Delete User
- **URL:** `DELETE /api/users/{id}`
- **Success Response:** `200 OK` + Success message
- **Error Response:** `404 Not Found` if user doesn't exist

#### 6. Get User Statistics
- **URL:** `GET /api/users/stats`
- **Response:** Database statistics and user counts

#### 7. Check Email Availability
- **URL:** `GET /api/users/check-email?email=test@example.com`
- **Response:** Whether email is already in use

### 🔥 Original Hello Endpoints (Still Working!)

#### Simple Hello
- **URL:** `GET /api/hello`
- **Response:** `"Hello, Backend Developer!"`

#### Personalized Hello
- **URL:** `GET /api/hello/{name}`
- **Response:** `"Hello, {name}! Welcome to Spring Boot!"`

#### Hello with JSON
- **URL:** `POST /api/hello`
- **Request Body:** `{"name": "Developer"}`
- **Response:** `"Hello, Developer! You made a POST request!"`

#### API Status
- **URL:** `GET /api/status`
- **Response:** JSON with API status and metadata

## 🚀 How to Run

### Prerequisites
- Java 17+ installed
- Maven 3.6+ installed

### Running the Application
```bash
# Clone the repository
git clone https://github.com/Mohammedf2017/hello-api.git
cd hello-api

# Run the application
mvn spring-boot:run

# Application will start on http://localhost:8080
```

### Accessing H2 Database Console
1. **URL:** http://localhost:8080/h2-console
2. **JDBC URL:** `jdbc:h2:file:./data/hello-api-db`
3. **Username:** `sa`
4. **Password:** (leave empty)

## 🧪 Testing the API

### Using cURL Commands

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","age":30}'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith","email":"jane@example.com","age":28}'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1

# Get statistics
curl http://localhost:8080/api/users/stats
```

### Using Browser (GET requests only)
- http://localhost:8080/api/users
- http://localhost:8080/api/users/1
- http://localhost:8080/api/users/stats
- http://localhost:8080/api/hello
- http://localhost:8080/api/status

## 🏗️ Architecture

### Application Layers
```
┌─────────────────────────────────────┐
│          REST Controllers           │ ← HTTP Endpoints
├─────────────────────────────────────┤
│            Service Layer            │ ← Business Logic
├─────────────────────────────────────┤
│          Repository Layer           │ ← Data Access
├─────────────────────────────────────┤
│              JPA/Hibernate          │ ← ORM Framework
├─────────────────────────────────────┤
│             H2 Database             │ ← Data Storage
└─────────────────────────────────────┘
```

### Key Components
- **UserController** - REST API endpoints
- **UserService** - Business logic and validation
- **UserRepository** - Database operations
- **User Entity** - Database table mapping
- **H2 Database** - Embedded SQL database

## 🔧 Features

### Data Persistence
- ✅ **Automatic table creation** from JPA entities
- ✅ **ACID transactions** for data consistency
- ✅ **Primary key auto-generation**
- ✅ **Automatic timestamps** (created_at, updated_at)
- ✅ **Data validation** with Jakarta Validation
- ✅ **Unique constraints** (email uniqueness)

### API Features
- ✅ **RESTful design** following HTTP standards
- ✅ **Proper HTTP status codes** (200, 201, 400, 404, 500)
- ✅ **JSON request/response** handling
- ✅ **Input validation** with meaningful error messages
- ✅ **Error handling** with consistent response format
- ✅ **Query parameters** for filtering
- ✅ **Comprehensive logging** with SQL statement visibility

### Development Features
- ✅ **Hot reload** with Spring Boot DevTools
- ✅ **Database console** for development/debugging
- ✅ **Detailed error messages** in development mode
- ✅ **SQL logging** for query optimization

## 🎯 Learning Achievements

### Month 1 Foundation (Days 1-28)
- ✅ **56 algorithm problems solved** with perfect consistency
- ✅ **All fundamental data structures mastered**
- ✅ **Essential patterns learned** (Two Pointer, Sliding Window, etc.)
- ✅ **Interview-ready problem-solving skills**

### Month 2 Progress (Days 29-30)
- ✅ **Spring Boot mastery** - Framework setup and configuration
- ✅ **REST API development** - Multiple endpoint types
- ✅ **Database integration** - JPA, Hibernate, H2
- ✅ **Service architecture** - Proper layer separation
- ✅ **Error handling** - Professional exception management
- ✅ **Data validation** - Input validation and business rules
- ✅ **SQL understanding** - Generated queries and optimization
- ✅ **Professional debugging** - Compilation and runtime issues

## 🚀 Next Steps (Upcoming Days)

### Day 31-35: Advanced Features
- **Pagination and sorting** for large datasets
- **Advanced queries** with custom JPQL
- **Data relationships** (One-to-Many, Many-to-Many)
- **File upload/download** capabilities

### Week 2: Security & Authentication
- **Spring Security** integration
- **JWT authentication** for API access
- **Role-based authorization**
- **Password encryption** and security best practices

### Week 3-4: Production Ready
- **Dockerization** for deployment
- **PostgreSQL** integration for production
- **API documentation** with Swagger/OpenAPI
- **Unit and integration testing**

## 💡 Technical Challenges Overcome

### Day 29 Challenges
- ✅ **IntelliJ annotation recognition** - Solved by testing functionality over IDE display
- ✅ **Maven dependency management** - Verified with dependency tree
- ✅ **Spring Boot configuration** - Proper application.properties setup

### Day 30 Challenges
- ✅ **JPA entity mapping** - Automatic table creation from annotations
- ✅ **Exception handling hierarchy** - Proper catch block ordering
- ✅ **Service layer design** - Business logic separation
- ✅ **Repository integration** - Spring Data JPA magic methods

## 📊 Project Statistics
- **Total Files:** 7 Java classes + configuration
- **Lines of Code:** ~800+ lines of professional backend code
- **Database Tables:** 1 (USERS) with 7 columns
- **API Endpoints:** 12 total (7 user management + 5 hello endpoints)
- **Dependencies:** 4 major Spring Boot starters
- **Development Time:** 2 days (29-30) for complete database integration

## 👨‍💻 Developer Notes

This project represents the successful transition from algorithm practice (Month 1) to real application development (Month 2). Key insights:

1. **Foundation Matters** - The 56 algorithm problems provided the problem-solving confidence needed for backend development
2. **Spring Boot Magic** - Framework handles much complexity, but understanding the underlying concepts is crucial
3. **Database Integration** - JPA/Hibernate abstracts SQL but developers should understand generated queries
4. **Professional Architecture** - Layer separation (Controller → Service → Repository) is essential for maintainability
5. **Error Handling** - Proper exception management makes APIs production-ready

The project demonstrates enterprise-level backend development skills and readiness for more advanced features.

---

**Built by:** Mohammedf2017  
**Date Range:** July 28-29, 2025  
**Project:** 6-Month Backend Developer Roadmap  
**Status:** Day 30 COMPLETE - Database API Working! ✅  
**GitHub:** https://github.com/Mohammedf2017/hello-api  
**Next Milestone:** Advanced features and authentication