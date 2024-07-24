# Blog API

This is a RESTful API for a blog application built with Spring Boot.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL

### Database Setup

1. Install PostgreSQL if you haven't already.
2. Create a new database named `blogdb`:
3. Create a new user and grant privileges: CREATE USER newuser WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE blogdb TO newuser;

Make sure your PostgreSQL server is running on localhost:5432

This project uses Swagger for API documentation.
To view the Swagger UI, run the application and go to: http://localhost:8080/swagger-ui.html
