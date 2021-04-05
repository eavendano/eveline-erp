Release Notes
---

```bash
Release [SEMANTIC_VERSION] (dd/mm/aaaa) [Responsible engineer name]
* [User Story ID if possible] - First chage name or description.
* [User Story ID if possible] - Second chage name or description.
```
Release 1.3.3 (04/03/21) Emanuel Avendaño
* Security improvements:
  * Updagrade ESAPI dependency.
  * Update deprecated constant for XSS filtering.

Release 1.3.2 (21/03/21) Emanuel Avendaño
* Security improvements:
  * Prevent XSS with filters and headers.
  * Implement ESAPI for cleaning input.
  
Release 1.3.1 (12/03/21) Emanuel Avendaño
* New Provider Activate endpoint that supports a set instead of single values.

Release 1.3.0 (07/03/21) Byron Miranda, Emanuel Avendaño
* Product CRUD operations implemented.
* Provider CRUD operations maintenance and optimization.
* Unit testing implemented for both features.
* Heavy validation improvements and testing.
* DB script with new tables, indexes, triggers and validation definitions.
* Definition for N-to-N mapping and Id generation for identities in both product and provider definitons.
* Project dependency updates.

Release 1.2.0 (04/06/2020) Emanuel Avendaño

* Increase Spring Boot and Spring Retry version.

Release 1.1.0 (23/05/2020) Emanuel Avendaño

* Support: Add Docker and Docker-Compose support including JMX monitoring outside the container. Added support for Java
  Cryptography Extension. Incremented Version. Documentation updated.
* Updated Swagger to a basic form.
* Updated default error page.
* Include settings.xml
* Retry statements when the DB is down working properly.
* Solved error serialization problem.
* Set user timeout on PGSQL.
* Initial RetryPolicy and TransactionManager setup running. Missing testing in general. Whitelisted provider endpoint.
* Create flow of data for Provider GET with validations and exceptions. Advice error handler implemented. Swagger
  documentations should be working but not implemented yet to reflect Eveline contracts.
* Updated HikariCP settings.
* Provider entity setup.