package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.healthmedia.ws.common.error.ApplicationErrorCode;
import com.healthmedia.ws.common.error.BadArgumentsException;
import com.healthmedia.ws.common.error.BadArgumentsException.BadArgumentError;

/**
 * Example exception mapper. Maps to a SOAP fault object defined in our xsd.
 * 
 */
@Provider
public class BadArgumentsExceptionMapper extends AbstractErrorV1ExceptionMapper<BadArgumentsException> implements ExceptionMapper<BadArgumentsException> {

	@Override
	public Response toResponse(BadArgumentsException exception) {

		ErrorType fault = new ErrorType();
		fault.setReasons(new ReasonCollectionType());
		fault.setCode(ApplicationErrorCode.BAD_ARGUMENT.getCode());
		
		ReasonType reason = new ReasonType();
		reason.setLang(Locale.US.getLanguage());
		reason.setValue("Bad argument error");
		
		fault.getReasons().getReason().add(reason);
		
		fault.setSubErrors(new ErrorCollectionType());
		
		for(BadArgumentError error : exception.getBadArgumentErrors()) {
			
			ErrorType innerFault = new ErrorType();
			innerFault.setCode(error.getName());
			innerFault.setReasons(new ReasonCollectionType());
			
			ReasonType innerReason = new ReasonType();
			innerReason.setLang(Locale.US.getLanguage());
			innerReason.setValue(new StringBuffer((error.getValue() == null) ? "null" : error.getValue()).append(" is not valid.").toString());
			
			innerFault.getReasons().getReason().add(innerReason);
			
			fault.getSubErrors().getError().add(innerFault);
		}
		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
