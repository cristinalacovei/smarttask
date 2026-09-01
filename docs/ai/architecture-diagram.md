# SmartTask Architecture Diagram

## AI-Assisted Architecture Documentation

This document contains the final SmartTask architecture diagram created with AI assistance.

The diagram combines the main application flow, security components, AWS services, AI integration, and CI/CD pipeline.

## Final Architecture

```mermaid
flowchart TB

    USER["User Browser"]

    subgraph CICD["CI/CD"]
        GH["GitHub<br/>main branch"]
        CP["AWS CodePipeline"]
        CB["AWS CodeBuild"]
        ECR["Amazon ECR"]
    end

    subgraph EDGE["Public Entry Point"]
        CF["Amazon CloudFront<br/>HTTPS"]
        S3["Amazon S3<br/>Angular Frontend"]
        ALB["Application Load Balancer"]
    end

    subgraph ECS["Amazon ECS / AWS Fargate"]
        KC["Keycloak<br/>External IAM"]
        GW["API Gateway<br/>Spring Cloud Gateway"]
        EUR["Eureka Server<br/>Service Discovery"]

        US["User Service"]
        TS["Task Service"]
        AI["AI Service"]
    end

    RDS[("Amazon RDS<br/>PostgreSQL")]
    SM["AWS Secrets Manager"]
    CW["Amazon CloudWatch"]
    CM["AWS Cloud Map"]
    GEMINI["Google Gemini API"]

    USER -->|"HTTPS"| CF

    CF -->|"Static Angular files"| S3
    CF -->|"/api/*"| ALB
    CF -->|"/realms/*<br/>/admin/*<br/>/resources/*"| ALB

    ALB --> GW
    ALB --> KC

    GW -->|"Route /api/users/**"| US
    GW -->|"Route /api/tasks/**"| TS
    GW -->|"Route /api/ai/**"| AI

    GW --> EUR
    US --> EUR
    TS --> EUR
    AI --> EUR

    US --> RDS
    TS --> RDS
    KC --> RDS

    AI -->|"OAuth2 Client Credentials"| KC
    AI -->|"Authenticated M2M"| US
    AI -->|"Authenticated M2M"| TS
    AI -->|"Generate study plan"| GEMINI

    SM -.->|"Secrets"| KC
    SM -.->|"Secrets"| US
    SM -.->|"Secrets"| TS
    SM -.->|"Secrets"| AI

    GW -.->|"Logs"| CW
    US -.->|"Logs"| CW
    TS -.->|"Logs"| CW
    AI -.->|"Logs"| CW
    KC -.->|"Logs"| CW

    ECS -.-> CM

    GH -->|"Push to main"| CP
    CP --> CB

    CB -->|"Build & push Docker images"| ECR
    ECR -->|"Deploy images"| ECS

    CB -->|"Angular production build"| S3
    CB -->|"CloudFront invalidation"| CF
```

## Runtime Request Flow

```mermaid
sequenceDiagram
    participant U as User
    participant CF as CloudFront
    participant KC as Keycloak
    participant GW as API Gateway
    participant US as User Service
    participant TS as Task Service
    participant AI as AI Service
    participant G as Gemini
    participant DB as RDS PostgreSQL

    U->>CF: Open application
    CF-->>U: Angular frontend from S3

    U->>CF: Login
    CF->>KC: OIDC / Authorization Code + PKCE
    KC-->>U: Authenticated session + tokens

    U->>CF: API request with Bearer token
    CF->>GW: /api/*
    GW->>GW: Validate JWT

    alt Task operation
        GW->>TS: Forward request
        TS->>US: Resolve JWT subject to application user
        US->>DB: Read user mapping
        DB-->>US: User
        US-->>TS: User ID
        TS->>DB: Read/write owned tasks
        DB-->>TS: Task data
        TS-->>GW: Response
    else AI study plan
        GW->>AI: POST /api/ai/study-plan
        AI->>US: M2M authenticated request
        US->>DB: Resolve user
        DB-->>US: User
        US-->>AI: User ID
        AI->>TS: M2M authenticated request
        TS->>DB: Load user tasks
        DB-->>TS: Tasks
        TS-->>AI: Task data
        AI->>G: Generate study plan
        G-->>AI: Study plan
        AI-->>GW: AI response
    end

    GW-->>CF: API response
    CF-->>U: Result
```

## CI/CD Flow

```mermaid
flowchart LR
    DEV["Developer"] -->|"git push"| GH["GitHub main"]
    GH --> CP["AWS CodePipeline"]
    CP --> CB["AWS CodeBuild"]

    CB --> M1["Maven build<br/>Eureka"]
    CB --> M2["Maven build<br/>API Gateway"]
    CB --> M3["Maven build<br/>User Service"]
    CB --> M4["Maven build<br/>Task Service"]
    CB --> M5["Maven build<br/>AI Service"]
    CB --> NG["Angular build"]

    M1 --> D["Docker build"]
    M2 --> D
    M3 --> D
    M4 --> D
    M5 --> D

    D --> ECR["Amazon ECR"]

    ECR --> DE1["Deploy Eureka"]
    DE1 --> DE2["Deploy User Service"]
    DE2 --> DE3["Deploy Task Service"]
    DE3 --> DE4["Deploy AI Service"]
    DE4 --> DE5["Deploy API Gateway"]

    NG --> S3["Amazon S3"]
    S3 --> INV["CloudFront invalidation"]
```

## Security Flow

```mermaid
flowchart LR
    USER["Student / Admin"]
    FRONT["Angular"]
    KC["Keycloak"]
    GW["API Gateway"]
    SERVICE["Protected Microservice"]
    INTERNAL["Internal Service Endpoint"]

    USER --> FRONT
    FRONT -->|"Authorization Code + PKCE"| KC
    KC -->|"JWT access token"| FRONT
    FRONT -->|"Bearer JWT"| GW
    GW -->|"Validated JWT"| SERVICE

    AI["AI Service"] -->|"client_credentials"| KC
    KC -->|"Service JWT"| AI
    AI -->|"ROLE_AI_SERVICE"| INTERNAL
```

## Main Components

### Application

- Angular frontend
- API Gateway
- Eureka Server
- User Service
- Task Service
- AI Service
- Keycloak
- PostgreSQL
- Google Gemini

### AWS Services

- Amazon ECS
- AWS Fargate
- Amazon ECR
- Amazon RDS
- Amazon S3
- Amazon CloudFront
- Application Load Balancer
- AWS Secrets Manager
- Amazon CloudWatch
- AWS Cloud Map
- AWS CodePipeline
- AWS CodeBuild

## Notes

- CloudFront is the public HTTPS entry point.
- Angular is hosted in a private S3 bucket and served through CloudFront.
- Backend services run as Docker containers on ECS Fargate.
- Keycloak provides external IAM, users, roles, OAuth2 and OpenID Connect.
- Eureka provides application-level service discovery.
- AWS Cloud Map is used in the AWS infrastructure for service discovery/network addressing.
- RDS PostgreSQL is private and protected by Security Groups.
- Sensitive deployment values are stored in AWS Secrets Manager.
- AI Service uses OAuth2 Client Credentials for machine-to-machine communication.
- The AI Study Planner uses Google Gemini.
- A push to the `main` branch triggers the complete AWS CI/CD pipeline.
