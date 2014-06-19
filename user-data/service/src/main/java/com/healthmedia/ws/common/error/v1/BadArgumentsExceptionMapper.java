package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.healthmedia.ws.common.error.ApplicationErrorCode;
import com.healthmedia.ws.common.error.BadArgumentsException;
import com.healthmedia.ws.common.error.BadArgumentsException.BadArgumentError;
import com.healthmedia.ws.common.v1.I18NTextType;

/**
 * Example exception mapper. Maps to a SOAP fault object defined in our xsd.
 * 
 */
@Provider
public class BadArgumentsExceptionMapper extends AbstractErrorV1ExceptionMapper<BadArgumentsException> {

	@Override
	public Response toResponse(BadArgumentsException exception) {

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
		subCode.setValue(ApplicationErrorCode.BAD_ARGUMENT.getCode()); // this should be an approved enum
		
		faultCode.setSubCode(subCode);
		fault.setCode(faultCode);
		//
		// description of the error in multiple languages
		//
		FaultReasonType reason = new FaultReasonType();
		
		I18NTextType text = new I18NTextType();
		text.setLanguage(Locale.US.getLanguage());
		text.setValue("invalid arguments passed to service");
		reason.getText().add(text);
		
		fault.setReason(reason);
		//
		// application specific error message
		//
		DetailType detail = new DetailType();
		for(BadArgumentError error : exception.getErrorMessages()) {
			detail.getAny().add(error);
		}
		fault.setDetail(detail);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
