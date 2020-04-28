Eveline ERP
---

[![Eveline ERP Version](https://img.shields.io/badge/version-1.0.0-blue?style=for-the-badge)](https://img.shields.io/badge/version-1.0.0-blue?style=for-the-badge)

## Purpose
Eveline ERP backend project. This project provides an interface for other Backend or Frontend internal 
projects to communicate with the ERP database and provide insights and operation contracts for a better
user experience for our customers.

---

## Getting Started

### Technology Stack
- [Java 13](https://www.oracle.com/technetwork/java/archive-139210.html)
- [Spring Boot 2.2.5](https://spring.io/projects/spring-boot)

### Installation Guide

#### Local PosgreSQL installation

Installation instructions for posgresql in MacOSX with Homebrew can be found [here.](https://wiki.postgresql.org/wiki/Homebrew)

#### Local Docker build
```

cd {projectdir}
docker build -t eveline-erp .
docker run -p 127.0.0.1:80:8080/tcp -p 127.0.0.1:7070:7070/tcp eveline-erp:latest

curl -X GET http://localhost/health
curl -X GET http://localhost:7070/metrics

```

### Build
```bash
./mvnw clean compile test
```

### Deploy
```bash
pending
```

---

## Software Environment

> Architecture and external tools of the production and development environments.

### Architecture
```mermaid
graph TD;
    UI --> API;
    >Queue] --> API;
    API --> Queue;
    API --> ((DB));
```

### Servers
|Integration|Development|Production|
|-----------|-----------|----------|
|           |           |          |

###Common Operations

####How to restart the application without a deployment.

### Known clients

### Dependencies

### Cron Jobs
> The cronjob that is running

```bash
0,15,30,45 * * * * <The cronjob that is running>
```

> Servers where the cronjob is running

|Development|Production|
|-----------|----------|
|dev01      |prod01    |

### Queues
- [Queue #1](#)
- [Queue #2](#)
- ...

### Pipeline
- [Pipeline](#)

### Monitoring
- This bundles a [JMX to Prometheus agent](https://github.com/prometheus/jmx_exporter) which scrapes and exports JMX targets via HTTP server.
- [SUMO API Log Dashboard](#)
- [SUMO Access Log Dashboard](#)
- [Grafana Dashboard](#)

#### Log Files
Application logs are exposed in different ways:

* Via Sumo Logic -> TBD
* Integration and production servers under the /path/to/directory/logs directory

Dynamic Logging Settings
Also, log settings can be dynamically changed at runtime in both integration and production servers.

Simply change your logging settings in /path/to/directory/logs/logback.xml.
Any changes will take around 1 minute to get applied by the log manager.

---

## Monitoring

### Alerting
|Server Metrics|
|--------------|
|[CPU](#)|
|[ram memory](#)|
|[disk usage](#)|
|[Ping service VIP](#)|
|[Ping service Nodes](#)|
|[Ping service dependent dbs, for example /db-health endpoint](#)|
|[DLQ size](#)|
|[Queue size](#)|

|Application Metrics|
|-------------------|
|[HTTP Pool saturation](#)|
|[DB Pool saturation](#)|
|[Rabbit listeners saturation](#)|
|[JVM memory saturation](#)|

|Performance Metrics|
|-------------------|
|[HTTP 500](#)|
|[ERROR level logs](#)|
|[Alert/Warn ? Duration degradation outliers](#)|
|[Alert/Warn ? Request load increase outlier](#)|

---

## User Guide
> How can we use it? How do we know it is working?

### API documentation
[Endpoints](#)

### Troubleshooting
|Integration      |Production       |
|-----------------|-----------------|
|[Manager](#)     |[Manager](#)     |
|[Ping](#)        |[Ping](#)        |
|[Health Check](#)|[Health Check](#)|
|[DB Check](#)    |[DB Check](#)    |

### Known errors

---

## [Team Members](mailto:marketing&merchandising@backcountry.com)
| **Team Member**                                                 | **Role**                           |
| ----------------------------------------------------------------|------------------------------------|
|[:envelope: Mariana Olivares](mailto:marianapoh@gmail.com)       | :cop: Associate / Manager          |
|[:envelope: Jose Pablo Arata](mailto:josepablo191290@gmail.com)  | :monkey_face: Associate / Engineer |
|[:envelope: Emanuel Avendano](mailto:eavendano.ic@gmail.com)     | :monkey_face: Associate / Engineer |


### Reference Documentation
For further reference, please consider the following sections:

* [Semantic Versioning](https://semver.org/)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/maven-plugin/)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#production-ready)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#boot-features-security)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Boot Admin (Client)](https://codecentric.github.io/spring-boot-admin/current/#getting-started)
* [Spring Boot Admin (Server)](https://codecentric.github.io/spring-boot-admin/current/#getting-started)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#boot-features-amqp)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#boot-features-mongodb)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [Spring Web Services](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#boot-features-webservices)
* [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [Spring MongoDB Reference](https://docs.spring.io/spring-data/mongodb/docs/2.2.5.RELEASE/reference/html/#reference)
* [Spring AMQP Reference](https://docs.spring.io/spring-amqp/reference/html/#_reference)
* [RabbitMQ Concepts](https://www.rabbitmq.com/tutorials/amqp-concepts.html)
* [Spring Redis Reference](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#reference)
* [Redis Lettuce Integration](https://www.bytepitch.com/blog/redis-integration-spring-boot/)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-template-engines)
* [HikariCP Config Knobs](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Producing a SOAP web service](https://spring.io/guides/gs/producing-web-service/)
