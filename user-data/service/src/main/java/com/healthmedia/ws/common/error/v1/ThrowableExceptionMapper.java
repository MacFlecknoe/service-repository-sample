package com.healthmedia.ws.common.error.v1;

import java.util.Locale;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

import com.healthmedia.ws.common.error.ApplicationErrorCode;

public class ThrowableExceptionMapper extends AbstractErrorV1ExceptionMapper<Throwable> {

	private static Logger LOGGER = Logger.getLogger(ThrowableExceptionMapper.class);
	
	@Override
	public Response toResponse(Throwable exception) {
		//
		// log the error and return a generic fault to the client
		//
		LOGGER.error(exception);
		
		ErrorType fault = new ErrorType();
		fault.setCode(ApplicationErrorCode.SERVER_ERROR.getCode());
		
		ReasonType reason = new ReasonType();
		reason.setLang(Locale.US.getLanguage());
		reason.setValue("The server has experienced an error.");
		
		fault.getReason().add(reason);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
