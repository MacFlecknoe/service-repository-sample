package com.healthmedia.ws.common.error.v1;

import javax.ws.rs.core.Response;

import com.healthmedia.ws.common.v1.I18NTextType;

public class GenericErrorExceptionMapper extends AbstractErrorV1ExceptionMapper<GenericErrorException> {

	@Override
	public Response toResponse(GenericErrorException exception) {
		
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
		subCode.setValue(exception.getCode());
		faultCode.setSubCode(subCode);
		
		fault.setCode(faultCode);

		FaultReasonType reason = new FaultReasonType();
		//
		// description of the error in multiple languages
		//
		for(GenericErrorException.ErrorMessage error : exception.getErrorMessages()) {
			I18NTextType text = new I18NTextType();
			text.setLanguage(error.getLocale().toLanguageTag());
			text.setValue(error.getMessage());
			reason.getText().add(text);
		}
		fault.setReason(reason);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}