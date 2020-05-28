Release Notes
---

Release 1.1.0 (23/05/2020) Emanuel Avendaño
 * Support: Add Docker and Docker-Compose support including JMX monitoring outside the container. Added support for Java Cryptography Extension. Incremented Version. Documentation updated.
 * Updated Swagger to a basic form.
 * Updated default error page.
 * Include settings.xml
 * Retry statements when the DB is down working properly.
 * Solved error serialization problem.
 * Set user timeout on PGSQL.
 * Initial RetryPolicy and TransactionManager setup running. Missing testing in general. Whitelisted provider endpoint.
 * Create flow of data for Provider GET with validations and exceptions. Advice error handler implemented. Swagger documentations should be working but not implemented yet to reflect Eveline contracts.
 * Updated HikariCP settings.
 * Provider entity setup.