# NoteManager Application

## Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [API Documentation](#api-documentation)
  - [Authentication APIs](#authentication-apis)
  - [Note Management APIs](#note-management-apis)
- [Authorization](#authorization)
- [Development](#development)
  - [Folder Structure](#folder-structure)
- [Contact](#contact)

---

## About the Project

The **NoteManager** application is a RESTful service designed to allow users to manage their notes efficiently. With built-in user authentication, the application ensures that only authorized users can create, read, update, delete, and share notes.

---

## Features

- **User Authentication**: Secure login and signup with JWT-based authentication.
- **Note Management**: Create, read, update, and delete notes.
- **Search**: Search notes by title or content.
- **Sharing**: Share notes with other users via email.

---

## Technologies Used

- **Backend Framework**: Spring Boot
- **Database**: PostgreSQL
- **Security**: Spring Security, JWT (JSON Web Tokens)
- **Testing**: JUnit, Spring Boot Test

---

## Choice of Framework, Database, and Tools
- **Backend Framework**: Spring Boot
Spring Boot was chosen due to its simplicity, ease of setup, and extensive support for building scalable RESTful applications. It enables rapid development by offering a wide range of auto-configurations and integrations with various tools, such as Spring Security for authentication and Spring Data JPA for database access.

- **Database**: PostgreSQL
PostgreSQL was selected as the relational database for this application because of its robustness, data integrity, and advanced querying capabilities. It is highly compatible with Spring Data JPA, allowing seamless integration into the Spring Boot application.

- **Security**: Spring Security & JWT
Spring Security is used to handle authentication and authorization in the application. JSON Web Tokens (JWT) are utilized for stateless authentication, allowing users to securely log in and perform actions on the notes with minimal server-side storage of session information.

- **Testing**: JUnit & Spring Boot Test
JUnit and Spring Boot Test are used for unit testing and integration testing of the application. These tools ensure that the application logic is correct and that the endpoints behave as expected under various scenarios.

## System Requirements

- **Java**: JDK 17 or higher
- **Maven**: Version 3.8.0 or higher
- **PostgreSQL**: Version 13 or higher

---

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/vrajpatel1411/NoteManager.git
cd notemanager
```

### 2. Configure the Database
- Set up a PostgreSQL database.
- Update the database credentials in `application.properties` or `application.yml`:

(Optional if you want to setup your own database)
```properties
spring.datasource.url=your_databaseCredential
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build and Run the Application
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Running Tests
To run the unit tests for the application, use the following command:
```bash
mvn test
```

The application will start at `http://localhost:8080`.

---

## API Documentation

The NoteManager API provides endpoints for user authentication and note management. All APIs require JSON request/response format.

### Authentication APIs

1. **POST `/api/auth/signup`**
   - **Description**: Registers a new user.
   - **Request Body**:
     ```json
     {
       "email": "user@example.com",
       "password": "password123"
     }
     ```

2. **POST `/api/auth/login`**
   - **Description**: Logs in a user and returns a JWT token.
   - **Request Body**:
     ```json
     {
       "email": "user@example.com",
       "password": "password123"
     }
     ```
   - **Response**:
     ```json
     {
       "token": "jwt_token"
     }
     ```

### Note Management APIs

**Note**: All endpoints require an `Authorization` header with the JWT token: `Authorization: Bearer <token>`

1. **POST `/api/notes/`**
   - **Description**: Creates a new note.
   - **Request Body**:
     ```json
     {
       "title": "My Note",
       "content": "This is the content of the note."
     }
     ```

2. **GET `/api/notes/`**
   - **Description**: Retrieves all notes created by the authenticated user.

3. **GET `/api/notes/{id}`**
   - **Description**: Retrieves a specific note by its unique ID.

4. **PUT `/api/notes/{id}`**
   - **Description**: Updates an existing note's title or content.
   - **Request Body**:
     ```json
     {
       "title": "Updated Title",
       "content": "Updated Content"
     }
     ```

5. **DELETE `/api/notes/{id}`**
   - **Description**: Deletes a specific note by its unique ID.

6. **POST `/api/notes/{id}/share`**
   - **Description**: Shares a note with another user via email.
   - **Request Body**:
     ```json
     {
       "email": "recipient@example.com"
     }
     ```

7. **GET `/api/notes/search?q=`**
   - **Description**: Searches for notes containing the query string in the title or content.
   - **Query Parameter**: `q` - The search term.

---

## Authorization

- **JWT Token**: Use the `/api/auth/login` endpoint to retrieve a token.
- Include the token in the `Authorization` header for all note management requests:
  ```
  Authorization: Bearer <token>
  ```


## Development

### Folder Structure

```
notemanager/
├── src/
│   ├── main/
│   │   ├── java/org/vrajpatel/notemanager/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── exception/
│   │   ├── resources/
│   │       ├── application.properties
│   ├── test/
```


## Contact

**Vraj Patel**  
- Email: pvraj1411@gmail.com
- LinkedIn: [Vraj Patel](https://linkedin.com/in/vrajpatel)
- Portfolio : www.vrajpatel.dev
