service-repository
===========

Sample project which demonstrates how to structure xml schema and service contracts for use within other projects. 

Schema files are maintained in a separate project from the services that implement them. This allows the schema to be more easy reused. 

As the service contracts are written first and are used to derive java artifacts they can also be leveraged by QA teams to write service tests against before any implementation work has been done.