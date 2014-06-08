user-data-service
===========

Example implementation of service interface generated from wadl. Can be deployed into vanilla Tomcat server.


curl -X GET -H "Accept: application/vnd.com.healthmedia.user-data+json;version=1.0" http://localhost:8080/user-data-service/user-data/11

returns JAXBException occurred : unable to marshal type "com.healthmedia.ws.entity.userdata.v1.UserDataType" as an element because it is missing an @XmlRootElement annotation.
