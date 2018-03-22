# Estafet Microservices Scrum Gateway API

## What is this?
This microservice is serving as a API Gateway in Estafet Scrum Demo Application.
The API Gateway is used as a single entry point for all microservices.
The API Gateway handles requests and then they are proxied/routed to the appropriate service.
It's important not to confuse a microservice API gateway with API management. 
What a microservices API Gateway provides is a front end used to access the microservices underneath—there is no support for publishing, promoting, or administering services at any significant level.

Each microservice has it's own git repository, but there is a master git repository that contains links to all of the repositories [here](https://github.com/Estafet-LTD/estafet-microservices-scrum).
## Getting Started
You can find a detailed explanation of how to install this (and other microservices) [here](https://github.com/Estafet-LTD/estafet-microservices-scrum#getting-started).

## Supported Camel Routes

### Project Microservice


To retrieve all of the projects
```
GET http://localhost/project-api/project
```

To retrieve project with specific id
```
GET http://localhost/project-api/project/id
```

To create a new project
```
POST http://localhost/project-api/project
```

### Sprint Microservice


To retrieve sprint with specific id
```
GET http://localhost/sprint-api/sprint/id
```

To retrieve all project sprints by passing project id
```
GET http://localhost/sprint-api/project/{id}/sprints
```

### Story Microservice


To retrieve specific story by passing story id
```
GET http://localhost/story-api/story/id
```

To retrieve all project stories by passing project id
```
GET http://localhost/story-api/project/{id}/stories
```

To create new project story by passing project id
```
POST http://localhost/story-api/project/{id}/story
```

## Gateway Block Schema

![alt tag](https://github.com/Estafet-LTD/estafet-microservices-scrum/blob/master/GatewayBlockSchema.jpg)

## Environment Variables
```
ENABLE_TRACER = false
PROJECT_GATEWAY_SERVICE_URI = http://gateway.microservices-scrum.svc:8080/project-api
SPRINT_GATEWAY_SERVICE_URI = http://gateway.microservices-scrum.svc:8080/sprint-api
STORY_GATEWAY_SERVICE_URI = http://gateway.microservices-scrum.svc:8080/story-api
```

