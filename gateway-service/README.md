# gateway-service
This service enables CRUD operations over REST for Employee entities

## Running tests

```
.\gradlew test
```

## Building jar

```
.\gradlew clean build
```

## Deploying docker

```
docker-compose up --build
```

## Environment Variables

Create a `.env` file in main directory with the following variables:

```
PROFILE=
DB_USER=
DB_SCHEMA=
DB_PASSWORD=
DB_NAME=
KC_HTTP_PORT=
KC_URL=
KC_REALM=
KC_CLIENT_ID=
KC_CLIENT_SECRET=
KC_ADMIN=
KC_ADMIN_PASSWORD=
```

## Endpoints 

### Health Check

```
GET /actuator/health
```
Returns the health status of the service.

### Inforamtion

```
GET /actuator/info
````
Returns the information about the service.

### Exchange login, password on jwt token

```
POST /auth/login
```
Request body:
```json
{
  "username": "test",
  "password": "test"
}
```

### Exchange login, password on jwt token

```
POST /auth/login
```
Request body:
```json
{
  "username": "test",
  "password": "test"
}
```

### Exchange refresh token on jwt token

```
POST /auth/refresh
```
Request body:
```json
{
  "refreshToken": "test"
}
```
### Get user info

```
GET /auth/me
```
Request headers:
```
Authorization: Bearer <token>
```
### Logout from the system

```
POST /auth/logout
```
Request headers:
```
Authorization: Bearer <token>
```
Request body:
```json
{
  "refreshToken": "test"
}
```