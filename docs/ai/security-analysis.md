# AI-Assisted Security Analysis

## Purpose

Artificial Intelligence was used as a security review assistant during SmartTask development.

The goal was to identify security risks, validate the existing security model, and improve cloud configuration without granting unnecessarily broad permissions.

## 1. External IAM and authentication

SmartTask uses Keycloak as an external Identity and Access Management system.

The Angular frontend uses OAuth2 / OpenID Connect with:

```text
Authorization Code Flow + PKCE (S256)
```

The frontend client is public and does not require a client secret.

Backend microservices act as OAuth2 Resource Servers and validate JWT access tokens.

## 2. Roles and authorization

Application authorization uses Keycloak realm roles such as:

```text
STUDENT
ADMIN
AI_SERVICE
```

Spring Security converts these roles to Spring authorities such as:

```text
ROLE_STUDENT
ROLE_ADMIN
ROLE_AI_SERVICE
```

Method-level rules are enforced using `@PreAuthorize`.

## 3. Machine-to-machine security

Internal communication from `ai-service` uses OAuth2 Client Credentials.

```text
AI Service
   |
   | client_credentials
   v
Keycloak
   |
   | service access token
   v
Protected internal endpoints
```

This avoids using a normal student's access token for service-to-service operations.

## 4. Task ownership and IDOR protection

The backend does not trust the browser to provide the owner of a task.

During task creation:

```text
JWT subject
   |
   v
User Service
   |
   v
Application User ID
   |
   v
Task.userId
```

For read, update, and delete operations, the backend verifies that the task belongs to the authenticated user.

This reduces the risk of insecure direct object reference (IDOR) style vulnerabilities.

## 5. Secret management

Sensitive production values are not intentionally stored in Git.

Examples include:

- Gemini API key
- Keycloak client secrets
- database credentials
- Keycloak administrator credentials

AWS Secrets Manager and environment variables are used for production configuration.

Example:

```yaml
client-secret: ${AI_SERVICE_CLIENT_SECRET}
```

## 6. Git repository secret review

Before the repository was made public, sensitive local/runtime files were excluded.

The root `.gitignore` includes entries such as:

```text
.env
infrastructure/keycloak/data/
infrastructure/keycloak/export/smarttask-realm.json
```

The unsanitized Keycloak realm export was excluded because it contained sensitive credential-related information.

Tracked files were also checked for common patterns associated with:

- AWS access keys
- Gemini API keys
- GitHub tokens
- private keys

No matching credentials were found in the tracked repository during that check.

## 7. AWS least-privilege IAM

The CI/CD system does not use administrator permissions for normal operation.

Dedicated permissions were created for CodeBuild and CodePipeline.

### CodeBuild

CodeBuild is allowed to perform the operations required by the pipeline, including:

- authenticate to Amazon ECR;
- push SmartTask Docker images;
- upload the Angular build to the SmartTask S3 bucket;
- invalidate the SmartTask CloudFront distribution.

### CodePipeline

CodePipeline is allowed to perform the ECS deployment operations required by the SmartTask services, including:

- describe ECS services;
- describe task definitions;
- register task definitions;
- update ECS services;
- pass the required ECS IAM roles.

## 8. Database network security

Amazon RDS PostgreSQL is deployed privately.

The database is not intended to accept direct public internet traffic.

Access is restricted using AWS Security Groups.

```text
Internet
   X
   |
   v
RDS PostgreSQL

ECS Security Group
   |
   | TCP 5432
   v
RDS Security Group
```

## 9. HTTPS and public entry point

The browser-facing application is served through Amazon CloudFront using HTTPS.

CloudFront provides a single public HTTPS entry point for:

- Angular static content
- API requests
- Keycloak browser endpoints

This avoids browser mixed-content problems.

## 10. CORS and redirect security

CORS configuration was restricted to the expected frontend origins, including the deployed CloudFront origin.

Keycloak redirect and post-logout redirect URIs were explicitly configured for the local and deployed frontend URLs.

## Security validation approach

AI recommendations were treated as suggestions, not as authoritative security decisions.

Changes were validated using:

- deployed application behavior;
- Keycloak configuration;
- IAM policies;
- Security Groups;
- AWS logs;
- HTTP responses;
- source-code review.

This combination of AI-assisted review and manual verification was used to reduce security risks while preserving the existing architecture.
