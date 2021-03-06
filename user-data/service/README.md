user-data-service
===========

Example implementation of service interface generated from wadl. Can be deployed into vanilla Tomcat server.

Some custom mapping of the marshalling provider needed to be done in order for this to work properly.

 	1. The DTO root elements needed to have root element names assigned to them as autogenerated classes are not marked as XmlRootElements by JAXB 
 	2. The JSON provider needed to be configured to ignore namespaces (this opens up the api up to naming collisions and so should be mapped to shorter names instead)
 	3. The wrapper element for returned json objects needed to be dropped
 	 
 See https://github.com/MacFlecknoe/service-repository-sample/blob/master/user-data/service/src/main/webapp/WEB-INF/beans.xml for details.


Generated REST interface:

```
/**
 * Created by Apache CXF WadlToJava code generator
**/
package com.healthmedia.ws.service.userdata.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import com.healthmedia.ws.entity.user.v1.UserDataCollectionType;
import com.healthmedia.ws.entity.user.v1.UserDataType;
import java.util.Date;

@Path("/user-data/")
public interface UserDataService {

    @GET
    @Produces({"application/vnd.healthmedia.user-data-collection+json;version=1.0", "application/vnd.healthmedia.user-data-collection+xml;version=1.0" })
    UserDataCollectionType findByQuery(@QueryParam("query") String query, @QueryParam("updateDate.start") Date updateDate_start, @QueryParam("updateDate.end") Date updateDate_end, @QueryParam("data.name") String data_name, 
                @QueryParam("user.id") String user_id);

    @POST
    @Consumes({"application/vnd.healthmedia.user-data+json;version=1.0", "application/vnd.healthmedia.user-data+xml;version=1.0" })
    @Produces({"application/vnd.healthmedia.user-data+json;version=1.0", "application/vnd.healthmedia.user-data+xml;version=1.0" })
    UserDataType create(UserDataType userdatatype);

    @POST
    @Consumes({"application/vnd.healthmedia.user-data-collection+json;version=1.0", "application/vnd.healthmedia.user-data-collection+xml;version=1.0" })
    @Produces({"application/vnd.healthmedia.user-data-collection+json;version=1.0", "application/vnd.healthmedia.user-data-collection+xml;version=1.0" })
    UserDataCollectionType createCollection(UserDataCollectionType userdatacollectiontype);

    @GET
    @Produces({"application/vnd.healthmedia.user-data+json;version=1.0", "application/vnd.healthmedia.user-data+xml;version=1.0" })
    @Path("/{id}")
    UserDataType findById(@PathParam("id") String id);

}
```
