Eveline ERP
---

[![Eveline ERP Version](https://img.shields.io/badge/version-1.5.0-blue?style=for-the-badge)](https://img.shields.io/badge/version-1.5.0-blue?style=for-the-badge)

## Purpose

Eveline ERP backend project. This project provides an interface for other Backend or Frontend internal projects to
communicate with the ERP database and provide insights and operation contracts for a better user experience for our
customers.

---

## Getting Started

### Technology Stack

- [Java 16](https://www.oracle.com/technetwork/java/archive-139210.html)
- [Spring Boot 2.5.1](https://spring.io/projects/spring-boot)
- [HikariCP](https://github.com/brettwooldridge/HikariCP)

### Installation Guide

#### Local PosgreSQL installation

Installation instructions for Postico in MacOSX can be found [here.](https://eggerapps.at/postico/)
To configure your local installation of Postico with Docker, follow this [steps.](https://www.upnxtblog.com/index.php/2019/06/03/how-do-i-connect-to-postgresql-running-on-host-from-docker-container/)
You can find the files in the previous doc in the path `/usr/local/var/postgres`.

### Build

```bash
./mvnw clean compile test
```

### How to run manually the jar

This works when the java version uses is 13. Also, is recommended to check the active profile for Spring and the JVM
memory to be allocated. Watch out with the version at the end of the command.

```bash
./mvnw clean compile test package
java -Dspring.profiles.active=default -Xms1g -Xmx1g -Xshare:off -XX:MinHeapFreeRatio=40 -XX:MaxHeapFreeRatio=70 -XX:+FlightRecorder -XX:+UseParallelGC -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar target/eveline-1.0.0.jar
```

### How to run on IntelliJ

In the `Run/Debug Configurations` window add a new Spring Boot Configuration. When selecting the main class, brouse
`EvelineApplication.java` and in the VMOptions section, add the following:

```bash
-Dspring.profiles.active=default
-Xms1g
-Xmx1g
-Xshare:off
-XX:MinHeapFreeRatio=40
-XX:MaxHeapFreeRatio=70
-XX:+FlightRecorder
-XX:+UseParallelGC
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.rmi.port=9010
-Dcom.sun.management.jmxremote.local.only=false
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

#### Local Docker builds

So far, there are a couple of strategies to run the project with docker. The first one which involves building the
docker image manually, which is written down bellow but is a bit cumbersome and involves specific knowledge of docker
commands on docker network (even when there is no network command bellow ;]), build images, tagging, container
information, JMX, volumes and running the images themselves, which is tiring; if you're willing to try it and understand
what is going on bellow, be my guest.

```
cd {projectdir}
docker build -f ./docker/Dockerfile -t eveline-erp .
docker run -e "JAVA_OPTIONS=-Dspring.profiles.active=default -Xms1g -Xmx1g -Xshare:off -XX:MinHeapFreeRatio=40 -XX:MaxHeapFreeRatio=70 -XX:+FlightRecorder -XX:+UseParallelGC -Dcom.sun.management.jmxremote.access.file=/root/jmc/jmxremote.access -Dcom.sun.management.jmxremote.password.file=/root/jmc/jmxremote.password -Djava.rmi.server.hostname=0.0.0.0 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=true -Dcom.sun.management.jmxremote.ssl=false" -p 127.0.0.1:8080:8080/tcp -p 127.0.0.1:9010:9010/tcp eveline-erp:latest

curl -X GET http://localhost:8080/eveline-erp/docs/index.html
```

Before explaining the next strategy, let me explain some commands that might help the next steps with Docker and
directly influence the development process. With the build command, you create the image to run in the container; then
you run it with the `run` command; once the image builds, some details don't change even when you build the image again,
meaning you should keep a good track of the current image you want to test.

As a suggestion, we recommend deleting dangling or unused images on your local machine, default, or intentionally
created networks by Docker and containers as well. Be careful with volumes, though; those might be very important for
your testing sessions or other Docker images.

You can check the official documentation for the following commands since they might change in the future, but initially
we suggest the following three:

```bash
docker image ls
docker image rm [IMAGE]
docker image prune
```

With the `ls` command, you can get a list of all the images in your machine. That way, you can get the image name or ID
to delete it with the `rm` command. The `prune` command is fantastic since it removes all the unused images when you
have a ton of them; check the command's options and the official documentation to use it the most efficiently possible.

Then, we have the following:

```bash
docker network ls
docker network inspect [NETWORK]
docker network rm [NETWORK]
docker network prune
```

Docker can create a network where your containers can be attached and allow them to communicate. With the `ls` command,
you can check the existing networks and those not used. With the `inspect` command, you can check details of a network,
including the containers, their respective image, and even their IP inside the network. Sometimes you might require that
the network be deleted and achieve it with the `rm` command for a specific network or the
`prune` for multiple unused networks.

There is one last command, but this one is a bit dangerous:

```bash
docker system prune -a
```

This one will obliterate all the images and networks not being used. and I mean all of them, so if you're working with
other images or Docker processes, you must be careful because this command doesn't care about any other previous work
you might have been doing.

One last detail is the command `docker ps --no-trunc`, which will show you the exact command being used to run your
image in the container. Just in case you need to troubleshoot updated image settings like JVM parameters.

All of these tools are important for the second option to do a docker build:

```bash
cd {projectdir}
docker-compose up eveline-erp-default
```

This command will do all that annoying work for you. Isn't that awesome? We've configured a file called
docker-compose.yaml, which will respond to the docker-compose command that comes along with any Docker installation.
You'll find the services configured on that file, the network configuration with the subnet, and even gateways depending
on the driver used. This will run the project with the default Spring Profile on the application. The operation can be
extended to other environments and testing scenarios not to be annoyed by passing them along in the console with a high
chance of making a mistake. To troubleshoot, remove, and update your images and networks with compose, the previous
commands we discussed in detail will help. Sometimes you need to delete the default network docker-compose creates, use
a different network, your container is not connecting to the right network, IPs don't match, ports are no open, and many
issues might arise when running the image locally.

### Deploy

```bash
pending
```

---

## Software Environment

> Architecture and external tools of the production and development environments and release processes.

### Branch naming convention.

Regarding branches, `master` branch is the law and cannot be modified by anyone that is not authorized to do so. If
you're going to create a `feature` branch, we recommend using dots to separate your branch name. Is just a suggestion,
but we find it less intrusive than hyphens (-) or underscores(_). Examples: `provider.implementation`
, `fix.remote.db.configuration`,
`ui.integration`, etc.

### Release Notes process and Pull Requests.

When releasing, it's required for Release Notes to be updated and understand what changed and when. This section will
specify the steps to perform a release while wrapping up the development process. The reason to do the steps we're
suggesting and in that order is to prevent and minimize scenarios of cherry-picking and do rollbacks more comfortable in
case that something goes wrong on production so that we can go back to previous docker images, versions of the code, and
troubleshoot changes faster to respond to our clients in the best way possible and high quality.

* Initially, we expect that your feature is a fork from the `master` branch.
* While developing, I bet you might do many commits to your `feature` branch, which is completely normal, and you can
  still do that as usual. Remember before ending your day to `commit` and `push`.
* When you're sure to be done, please update the following files:
    1. Remember that we use `Semantic Versioning`. Go to the links at the bottom to read on how to update them. Comments
       when making the `pull request` will also be related to how to use it correctly.
    2. `README.md` update badge on line 4 with the right version.
    3. `eveline-swagger.json` line 6.
    4. `pom.xml` line 13.
    5. `application.yml` line 5.
    6. `Dockerfile` line 30
    7. `RELEASE_NOTES.md` with the respective notes of the release in the following format:
    ```bash
    Release [SEMANTIC_VERSION] (dd/mm/aaaa) [Responsible engineer name]
    * [User Story ID if possible] - First chage name or description.
    * [User Story ID if possible] - Second chage name or description.
    ```
* To merge your `feature` code with `master` branch, we recommend using a single commit. To achieve this, here is a list
  of steps that you can follow.
    1. Update the `RELEASE_NOTES.md` file that is on the root of this project. You can also do it while performing the
       merge step during step 5.
    2. Pull the origin `master` branch in your code to be up to date.
    3. Switch to the `master` branch and pull origin to be up to date as well.
    4. Create a "bubble" branch from `master` where the person responsible will perform the first merge.
       Example: `my.feature.branch.v2`.
    5. Perform the following list of commands:
       `git merge --squash my.original.feature.branch.with.the.ready.work`
       and then `git commit`. Before accepting the commit, remove all the "garbage" or unnecessary commit messages, and
       we suggest that you create a meaningful commit message to identify your feature branch.
    6. Submit a `pull request` on Github to merge your branch into master. If the team finds any issues, the developer
       will receive a notification through email or chat with the comments with observations, corrections, and
       suggestions specified on the `pull request`.

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

### Common Operations

#### How to restart the application without a deployment.

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

- [Dependency Monitoring](https://app.snyk.io/org/eavendano/projects)
- This bundles a [JMX to Prometheus agent](https://github.com/prometheus/jmx_exporter) which scrapes and exports JMX
  targets via HTTP server.
- [SUMO API Log Dashboard](#)
- [SUMO Access Log Dashboard](#)
- [Grafana Dashboard](#)

#### Log Files

Application logs are exposed in different ways:

* Via Sumo Logic -> TBD
* Integration and production servers under the /path/to/directory/logs directory

Dynamic Logging Settings Also, log settings can be dynamically changed at runtime in both integration and production
servers.

Simply change your logging settings in /path/to/directory/logs/logback.xml. Any changes will take around 1 minute to get
applied by the log manager.

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

## [Team Members](mailto:)

| **Team Member**                                                 | **Role**                           |
| ----------------------------------------------------------------|------------------------------------|
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
* [Official Docker Compose Network 3.8](https://docs.docker.com/compose/networking/)
* [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)

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
* [Docker Compose Networking Help](https://runnable.com/docker/docker-compose-networking)
* [Docker Bridge network tutorial](https://docs.docker.com/network/network-tutorial-standalone/)
* [Establish JMX connection to JVM running on docker](https://www.adam-bien.com/roller/abien/entry/how_to_establish_jmx_connection)
* [Docker Remove images, containers and volumes](https://www.digitalocean.com/community/tutorials/how-to-remove-docker-images-containers-and-volumes)
* [Docker Remove network](https://docs.docker.com/engine/reference/commandline/network_rm/)
* [Run Postgresql Local](https://medium.com/@martinlasek/tutorial-how-to-use-postgresql-88cddc858d9f)
* [Spring DataIntegrityViolationException Explained (ConstraintViolationException)](https://www.baeldung.com/spring-dataIntegrityviolationexception)
* [Programmatic Transaction Management](https://www.baeldung.com/spring-programmatic-transaction-management)
* [Overview of identifiers in Hibernate/JPA](https://www.baeldung.com/hibernate-identifiers)
* [JPA Many to Many Modeling](https://www.baeldung.com/jpa-many-to-many)
* [10 Excellent Ways to Secure Your Spring Boot Application](https://developer.okta.com/blog/2018/07/30/10-ways-to-secure-spring-boot)
* [Preventing XSS in a Spring Application](https://www.baeldung.com/spring-prevent-xss)