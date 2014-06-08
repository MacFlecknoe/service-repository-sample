user-data-service
===========

Example implementation of service interface generated from wadl. Can be deployed into vanilla Tomcat server.

Some custom mapping of the marshalling provider needed to be done in order for this to work properly.

 	1. The custom media types needed to be mapped to the default xml and json providers 
 	2. The DTO elements needed to be configured to be treated at XMLElements rather than RootElements
 	3. The JSON provider needed to be configured to ignore namespaces (this opens up the api up to naming collisions and so should be mapped to shorter names instead)
 	
 See beans.xml for details.