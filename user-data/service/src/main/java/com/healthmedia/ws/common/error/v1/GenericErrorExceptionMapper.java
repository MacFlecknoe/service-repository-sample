package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;

import com.healthmedia.ws.common.error.GenericErrorException;

public class GenericErrorExceptionMapper extends AbstractErrorV1ExceptionMapper<GenericErrorException> {

	@Override
	public Response toResponse(GenericErrorException exception) {
		
		ErrorType fault = new ErrorType();
		fault.setCode(exception.getCode());
		
		ReasonType reason = new ReasonType();
		reason.setLang(Locale.US.getLanguage());
		reason.setValue(exception.getMessage());
		
		fault.getReason().add(reason);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
