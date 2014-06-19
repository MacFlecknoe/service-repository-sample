package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.healthmedia.ws.common.v1.I18NTextType;

public class ThrowableExceptionMapper extends AbstractErrorV1ExceptionMapper<Throwable> {

	private static Logger LOGGER = Logger.getLogger(ThrowableExceptionMapper.class);
	
	@Override
	public Response toResponse(Throwable exception) {
		//
		// log the error and return a generic fault to the client
		//
		LOGGER.error(exception);
		
		FaultType fault = new FaultType();
		
		FaultCodeType faultCode = new FaultCodeType();
		//
		// responsible party or root cause
		//
		faultCode.setValue(FaultCodeEnum.RECEIVER);
		//
		// general category of the error
		//
		SubCodeType subCode = new SubCodeType();
		subCode.setValue(ApplicationErrorCode.SERVER_ERROR.getCode());
		
		faultCode.setSubCode(subCode);
		fault.setCode(faultCode);
		//
		// description of the error in multiple languages
		//
		FaultReasonType reason = new FaultReasonType();
		
		I18NTextType text = new I18NTextType();
		text.setLanguage(Locale.US.getLanguage());
		text.setValue("The server has experienced an error.");
		reason.getText().add(text);
		
		fault.setReason(reason);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
