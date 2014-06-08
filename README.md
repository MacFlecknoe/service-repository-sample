service-repository
===========

Sample project which demonstrates how to structure xml schema and service contracts for use within other projects. 

Schema files are maintained in a separate project from the services that implement them. This allows the schema to be more easy reused across multiple services.

The schema and service contracts are written as a predecessor to any implementation work. They are used to generate DTOs and service interfaces which are used by the implementation teams. They can also be leveraged by QA teams to write service tests before any implementation work has been done. This corrects the traditional development dependency QA has traditionally had and allows the entire team to work more collaboratively.