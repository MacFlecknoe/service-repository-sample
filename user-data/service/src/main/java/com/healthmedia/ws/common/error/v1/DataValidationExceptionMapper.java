package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.healthmedia.ws.common.error.ApplicationErrorCode;
import com.healthmedia.ws.common.error.DataValidationException;

/**
 * Example exception mapper. Maps to a SOAP fault object defined in our xsd.
 * 
 */
@Provider
public class DataValidationExceptionMapper extends AbstractErrorV1ExceptionMapper<DataValidationException> {

	@Override
	public Response toResponse(DataValidationException exception) {

		ErrorType fault = new ErrorType();
		fault.setCode(ApplicationErrorCode.VALIDATION_ERROR.getCode());
		
		ReasonType reason = new ReasonType();
		reason.setLang(Locale.US.getLanguage());
		reason.setValue("Data validation error");
		
		ReasonCollectionType reasons = new ReasonCollectionType();
		reasons.getReason().add(reason);
		fault.setReasons(reasons);
		
		fault.setSubErrors(new ErrorCollectionType());
		
		for(DataValidationException.DataValidationError error : exception.getDataValidationErrors()) {
			
			ErrorType innerFault = new ErrorType();
			innerFault.setCode(error.getCode());
			
			ReasonType innerReason = new ReasonType();
			innerReason.setLang(Locale.US.getLanguage());
			innerReason.setValue(error.getDescription());
			
			ReasonCollectionType subReasons = new ReasonCollectionType();
			subReasons.getReason().add(innerReason);
			innerFault.setReasons(subReasons);
			
			ErrorCollectionType subErrors = new ErrorCollectionType();
			subErrors.getError().add(innerFault);
			innerFault.setSubErrors(subErrors);
			
			fault.getSubErrors().getError().add(innerFault);
		}
		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}
}
