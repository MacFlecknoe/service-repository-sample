package com.healthmedia.ws.common.error.v1;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.healthmedia.ws.common.error.v1.ErrorType;

/**
 * Example exception mapper
 *
 */
@Provider
public class InvalidQueryExceptionMapper extends AbstractErrorV1ExceptionMapper<InvalidQueryException> {
	
	@Override
	public Response toResponse(InvalidQueryException exception) {

		ErrorType error = new ErrorType();
		
		error.setCode("INVALID_QUERY");
		error.setName("Invalid Query");

		return Response.status(Response.Status.BAD_REQUEST).entity(error).type(super.getMediaType()).build();
	}
}
