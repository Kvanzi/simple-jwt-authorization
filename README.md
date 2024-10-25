
# Simple JWT authorization

[![Developed by kvanzi](https://img.shields.io/badge/Developed%20by-kvanzi-%236DB33F)](https://github.com/kvanzi)
![JDK](https://img.shields.io/badge/JDK-23-%23E76F00)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-%236DB33F)
[![PostgreSQL](https://img.shields.io/badge/DBMS-PostgreSQL-%236DB33F)](https://www.postgresql.org/)

This project is a RESTful API with JWT (JSON Web Token) authentication, built using Spring Boot. It includes basic authentication and authorization mechanisms to secure access to the API's resources.

## Features
- User registration and login
- JWT token generation and validation
- Securing endpoints using JWT tokens
- Automatic token verification with every request
- Token refresh mechanism
## API Reference

#### Signup

```http
  POST /api/auth/signup
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` |       **Required**        |

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `password` | `string` |      **Required**         |

Returns user info.

#### Login

```http
  POST /api/auth/login
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` |       **Required**        |

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `password` | `string` |      **Required**         |

Returns user info and cookies with access and refresh tokens.

#### Get current user info

```http
  GET /api/users/me
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `Cookie: access; or Cookie: refresh;` | `string` |       **Required** — either access token or refresh token must be provided in the Cookie header.        |

Returns user info.
## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/kvanzi/simple-jwt-authorization.git
    ```

2. Navigate to the project directory:
    ```bash
    cd simple-jwt-authorization
    ```

3. Build and run the application:
    ```bash
    ./mvnw clean package
    ./mvnw spring-boot:run
    ```

## Configuration

Database connection settings and other parameters can be specified in the .env file:
```
#DATASOURCE
DATASOURCE_URL=Your Database Url
DATASOURCE_USERNAME=Your Database Username
DATASOURCE_PASSWORD=Your Database Password

#JWT
JWT_SECRET=Your JWT Secret Key
```