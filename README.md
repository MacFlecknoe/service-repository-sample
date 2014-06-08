service-repository
===========

Sample project which demonstrates how to structure xml schema and service contracts for use within other projects. 

Schema files are maintained in a separate project from the services that implement them. This allows the schema to be more easy reused across multiple services.

The schema and service contracts are written as a predecessor to any implementation work. These artifacts are used to generate the DTOs and service interfaces that the implementation teams will leverage using "contract to java" tools. 

The schema and service contracts can also be leveraged by QA teams to start writing tests before development has been done; development should no longer be a bottleneck to QA. 