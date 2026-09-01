# AI-Assisted Code Generation

## Purpose

Artificial Intelligence was used during the development of SmartTask as an assistant for generating, refining, and adapting code and configuration.

AI-generated suggestions were reviewed and adjusted to match the existing SmartTask architecture before being integrated into the project.

## Examples

### 1. Spring Security configuration

AI assistance was used to create and refine Spring Security configuration for the microservices.

Examples include:

- OAuth2 Resource Server configuration
- JWT validation
- extraction of Keycloak realm roles
- method-level authorization with `@PreAuthorize`
- API Gateway CORS configuration

The main application roles include:

- `STUDENT`
- `ADMIN`
- `AI_SERVICE`

### 2. OAuth2 machine-to-machine communication

AI assistance was used to configure OAuth2 Client Credentials for communication between internal microservices.

The main flow is:

```text
AI Service
   |
   | OAuth2 Client Credentials
   v
Keycloak
   |
   | Service JWT
   v
User Service / Task Service
```

The implementation includes:

- OAuth2 client registration
- `OAuth2AuthorizedClientManager`
- authenticated `RestClient` calls
- protected internal endpoints
- service roles for machine-to-machine authorization

### 3. Task ownership implementation

AI assistance was used while implementing the task security model.

The application does not trust a `userId` supplied by the frontend when creating a task. Instead, the authenticated user's identity is resolved from the JWT.

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
Task ownership
```

The same identity is used to verify ownership before task read, update, and delete operations.

### 4. AWS CI/CD configuration

AI assistance was used to create and refine the AWS CodeBuild `buildspec.yml`.

The final pipeline automatically:

1. builds the Spring Boot microservices;
2. builds the Angular frontend;
3. creates Docker images;
4. tags Docker images with `latest` and the Git commit short SHA;
5. pushes images to Amazon ECR;
6. uploads the Angular build to Amazon S3;
7. creates a CloudFront invalidation;
8. generates ECS image definition files;
9. deploys the microservices to Amazon ECS Fargate.

### 5. Docker and cloud configuration

AI assistance was also used to refine:

- Dockerfiles
- Docker Compose configuration
- ECS deployment configuration
- Eureka configuration for ECS rolling deployments
- AWS IAM policies required by CodeBuild and CodePipeline

## Example prompts

Examples of prompts used during development include:

> Generate a Spring Security configuration for a Spring Boot Resource Server using Keycloak realm roles.

> Configure OAuth2 Client Credentials communication between Spring Boot microservices.

> Review the task creation flow so that the backend derives the task owner from the authenticated JWT instead of trusting a userId from the client.

> Create an AWS CodeBuild buildspec that builds multiple Spring Boot services and an Angular frontend, creates Docker images, pushes them to ECR, and prepares ECS deployment artifacts.

## Human validation

AI-generated output was not integrated blindly.

Important changes were:

- reviewed manually;
- adapted to the existing project;
- compiled;
- deployed;
- tested in the running application;
- verified using application and AWS logs.
