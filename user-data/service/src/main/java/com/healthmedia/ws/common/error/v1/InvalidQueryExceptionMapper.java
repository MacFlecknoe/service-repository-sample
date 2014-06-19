package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.healthmedia.ws.common.v1.I18NTextType;

/**
 * Example exception mapper. Maps to a SOAP fault object defined in our xsd.
 * 
 */
@Provider
public class InvalidQueryExceptionMapper extends AbstractErrorV1ExceptionMapper<InvalidQueryException> {

	@Override
	public Response toResponse(InvalidQueryException exception) {

		FaultType fault = new FaultType();
		
		FaultCodeType faultCode = new FaultCodeType();
		//
		// responsible party or root cause
		//
		faultCode.setValue(FaultCodeEnum.SENDER);
		//
		// general category of the error
		//
		SubCodeType subCode = new SubCodeType();
		subCode.setValue("BadArguments"); // this should be an approved enum
		faultCode.setSubCode(subCode);
		
		fault.setCode(faultCode);
		//
		// description of the error in multiple languages
		//
		FaultReasonType reason = new FaultReasonType();
		
		I18NTextType text = new I18NTextType();
		text.setLanguage(Locale.US.getLanguage());
		text.setValue(new StringBuilder().append("Invalid Query: ").append(exception.getQuery()).toString());
		reason.getText().add(text);
		
		fault.setReason(reason);
		//
		// application specific error message
		//
		DetailType detail = new DetailType();
		detail.getAny().add(new ParameterType("query", exception.getQuery()));
		fault.setDetail(detail);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
