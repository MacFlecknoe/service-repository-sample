package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.healthmedia.ws.common.error.ApplicationErrorCode;
import com.healthmedia.ws.common.error.DataValidationException;
import com.healthmedia.ws.common.v1.I18NTextType;

/**
 * Example exception mapper. Maps to a SOAP fault object defined in our xsd.
 * 
 */
@Provider
public class DataValidationExceptionMapper extends AbstractErrorV1ExceptionMapper<DataValidationException> {

	@Override
	public Response toResponse(DataValidationException exception) {

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
		subCode.setValue(ApplicationErrorCode.VALIDATION_ERROR.getCode()); // this should be an approved enum
		
		faultCode.setSubCode(subCode);
		fault.setCode(faultCode);
		//
		// description of the error in multiple languages
		//
		FaultReasonType reason = new FaultReasonType();
		
		I18NTextType text = new I18NTextType();
		text.setLanguage(Locale.US.getLanguage());
		text.setValue(exception.getDescription());
		reason.getText().add(text);
		
		fault.setReason(reason);
		//
		// application specific error message
		//
		DetailType detail = new DetailType();
		for(DataValidationException.DataValidationError error : exception.getDataValidationErrors()) {
			detail.getAny().add(error);
		}
		fault.setDetail(detail);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
