# Security Configuration - Role-Based Access Control

## Overview
This document describes the security configuration implemented for the ChoreCircle backend application, which uses role-based access control (RBAC) to restrict access to sensitive endpoints.

## Roles
The application uses the following roles:
- **ADMIN**: Full access to all endpoints including user and role management
- **MODERATOR**: Read-only access to user and role information, cannot modify data
- **USER**: Basic user access (default role for regular users)

## Security Configuration

### 1. HTTP Security Configuration (`SecurityConfig.java`)
The main security configuration is defined in `SecurityConfig.java` with the following access rules:

#### Public Endpoints (No Authentication Required)
- `/api/auth/**` - Authentication endpoints (login, register, etc.)
- `/actuator/**` - Spring Boot Actuator endpoints
- `/api/users/simple` - Basic user information
- `/api/users/debug/**` - Debug endpoints

#### Admin and Moderator Access (Read Operations)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/exists/**` - Check if user exists
- `GET /api/users/search` - Search users
- `GET /api/users/by-role/**` - Get users by role
- `GET /api/users/{userId}/has-role/**` - Check user roles
- `GET /api/users/{userId}/roles` - Get user roles
- `GET /api/users/username/{username}/roles` - Get user roles by username
- `GET /api/roles` - Get all roles
- `GET /api/roles/{id}` - Get role by ID
- `GET /api/roles/name/{name}` - Get role by name
- `GET /api/roles/ordered` - Get all roles ordered
- `GET /api/roles/search` - Search roles
- `GET /api/roles/exists/{name}` - Check if role exists
- `GET /api/roles/{id}/users-count` - Get users count with role
- `GET /api/roles/name/{name}/users-count` - Get users count with role by name

#### Admin Only Access (Write Operations)
- `POST /api/users` - Create user
- `PUT /api/users/{id}/password` - Update user password
- `PUT /api/users/username/{username}/password` - Update user password by username
- `PUT /api/users/{id}/roles` - Update user roles
- `POST /api/users/{id}/roles` - Add role to user
- `DELETE /api/users/{id}/roles` - Remove role from user
- `PUT /api/users/{id}/enabled` - Enable/disable user
- `DELETE /api/users/{id}` - Delete user
- `DELETE /api/users/username/{username}` - Delete user by username
- `POST /api/roles` - Create role
- `PUT /api/roles/{id}/name` - Update role name
- `PUT /api/roles/name/{currentName}` - Update role name by current name
- `DELETE /api/roles/{id}` - Delete role
- `DELETE /api/roles/name/{name}` - Delete role by name
- `DELETE /api/roles/{id}/force` - Force delete role
- `DELETE /api/roles/name/{name}/force` - Force delete role by name

### 2. Method-Level Security (`@PreAuthorize`)
Additional security is implemented using Spring Security's method-level annotations:

#### UserController
- All read operations: `@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")`
- All write operations: `@PreAuthorize("hasRole('ADMIN')")`

#### RoleController
- All read operations: `@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")`
- All write operations: `@PreAuthorize("hasRole('ADMIN')")`

## Default Setup
The application automatically initializes:
1. Default roles (ADMIN, MODERATOR, USER)
2. An admin user with username "admin" and password "admin"

## Testing the Security Configuration

### 1. Test with Admin User
```bash
# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Use the returned JWT token to access protected endpoints
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Test with Regular User
```bash
# Create a regular user (requires admin token)
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password","roles":["USER"]}'

# Try to access protected endpoints (should fail)
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer USER_JWT_TOKEN"
```

### 3. Test Role-Based Access
```bash
# Create a moderator user
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"username":"moderator","password":"password","roles":["MODERATOR"]}'

# Moderator can read but not write
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer MODERATOR_JWT_TOKEN"  # Should succeed

curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer MODERATOR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password","roles":["USER"]}'  # Should fail
```

## Security Features
1. **JWT Authentication**: Stateless authentication using JWT tokens
2. **Role-Based Access Control**: Fine-grained access control based on user roles
3. **Method-Level Security**: Additional protection at the method level
4. **HTTP Method Restrictions**: Different permissions for different HTTP methods
5. **Public Endpoints**: Authentication endpoints remain publicly accessible

## Best Practices
1. Always use HTTPS in production
2. Regularly rotate JWT secrets
3. Implement proper password policies
4. Monitor and log security events
5. Regularly audit user permissions
6. Use strong passwords for admin accounts

## Troubleshooting
- If endpoints return 403 Forbidden, check user roles
- If endpoints return 401 Unauthorized, check JWT token validity
- Ensure roles are properly assigned to users
- Verify JWT token is included in Authorization header 