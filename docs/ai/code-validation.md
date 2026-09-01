# AI-Assisted Code Validation

## Purpose

Artificial Intelligence was used to review existing code, configuration, logs, and runtime behavior during SmartTask development.

The objective was to validate the implementation, identify the actual cause of failures, and avoid unnecessary architectural changes.

## Examples

### 1. Task authorization and ownership validation

The `TaskService` and `TaskController` implementations were reviewed to verify that:

- the authenticated user is derived from the JWT;
- task creation does not trust a client-supplied `userId`;
- `GET /api/tasks` returns the current user's tasks;
- reading a specific task verifies ownership;
- updating a task verifies ownership;
- deleting a task verifies ownership;
- internal AI endpoints are protected by a service role.

The ownership flow is:

```text
Authenticated Keycloak user
        |
        v
JWT subject
        |
        v
User Service lookup
        |
        v
Application User ID
        |
        v
Ownership validation
```

### 2. Eureka registration issue on ECS

During an ECS rolling deployment, `ai-service` temporarily became unavailable through the API Gateway.

The logs were analyzed with AI assistance.

The problem was traced to multiple ECS tasks using the same default Eureka instance identifier. During a rolling deployment, the old task could deregister the identifier used by the new task.

The solution was:

```yaml
eureka:
  instance:
    instance-id: ${spring.application.name}:${random.value}
```

This provides a unique Eureka registration for every running ECS task.

The same approach was later applied to:

- API Gateway
- User Service
- Task Service

### 3. AWS CodeBuild frontend failure

The backend compiled successfully in CodeBuild, but the Angular build initially failed at:

```text
npm ci
```

The build logs showed that `package.json` and `package-lock.json` were not synchronized for the clean Linux CI environment.

The frontend lock file was regenerated locally and committed.

After that change, the CodeBuild execution completed successfully.

### 4. Keycloak logout validation

The deployed frontend initially displayed:

```text
Invalid redirect uri
```

when the user logged out.

The Angular logout implementation was reviewed and already used:

```typescript
window.location.origin
```

Therefore, the application code was not unnecessarily changed.

The problem was traced to the Keycloak client's allowed post-logout redirect URI configuration. After adding the CloudFront application URL, logout worked correctly.

### 5. API Gateway CORS validation

The deployed frontend could perform GET requests, but some write operations returned `403`.

The API Gateway CORS configuration was reviewed and updated to allow the deployed CloudFront origin and the required HTTP methods.

After rebuilding and deploying the API Gateway, task create, update, and delete operations worked from the deployed Angular application.

## Validation methods

AI-assisted recommendations were checked using:

- Java build results
- Angular build results
- application logs
- AWS CloudWatch logs
- Eureka registration logs
- HTTP status codes
- browser testing
- ECR image tags
- ECS deployment status
- AWS CodePipeline executions
- AWS CodeBuild executions

The final decision to keep or reject a proposed change was based on the actual application behavior.
