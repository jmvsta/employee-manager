# admin-service
Employee management service

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
SECURITY_USER=
SECURITY_PASSWORD=
DB_USER=
DB_SCHEMA=
DB_PASSWORD=
DB_NAME=
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

### Get All Employees

```
GET /api/v1/employees
```
Returns all employees

### Get Employee by id

```
GET /api/v1/employees/:id
```
Returns employee by id


### Find Employees with filter

```
POST /api/v1/employees/find
```
Request body:
```json
{
  "name": "test",
  "teamName": "test",
  "teamLeadName": "test",
  "teamLeadId": 1,
  "teamLeadsOnly": true
}
```
Filteres employees by specified filter

### Create Employee

```
POST /api/v1/employees
```
Request body:
```json
{
  "id": 1,
  "name": "test",
  "team": "test",
  "teamLead": "test"
}
```
Creates employee

### Update Employee by id

```
PATCH /api/v1/employees/:id
```
Request body:
```json
{
  "id": 1,
  "name": "test",
  "team": "test",
  "teamLead": "test"
}
```
Updates employee partially 

### Delete Employee by id

```
DELETE /api/v1/employees/:id
```
Removes employee 

### Get Team by id

```
GET /api/v1/teams/:id
```
Returns team by id 

### Get All Teams

```
GET /api/v1/teams
```
Returns all teams

### Create Team

```
POST /api/v1/teams
```
Request body:
```json
{
  "id": 1,
  "name": "test",
  "team": "test",
  "teamLeadId": 1
}
```
Creates team

### Update Team by id

```
PATCH /api/v1/teams/:id
```
Request body:
```json
{
  "id": 1,
  "name": "test",
  "team": "test",
  "teamLeadId": 1
}
```
Updates team partially 

### Delete Team by id

```
DELETE /api/v1/teams/:id
```
Removes team 