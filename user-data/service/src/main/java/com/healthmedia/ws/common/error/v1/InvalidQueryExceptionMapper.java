package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;

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
		subCode.setValue("BadArguments");
		faultCode.setSubCode(subCode);
		
		fault.setCode(faultCode);
		//
		// description of the error in multiple languages
		//
		FaultReasonType reason = new FaultReasonType();
		I18NTextType text = new I18NTextType();
		text.setLang(Locale.US.getLanguage());
		text.setValue("Invalid Query Parameter");
		reason.getText().add(text);
		fault.setReason(reason);
		//
		// application specific error message
		//
		DetailType detail = new DetailType();
		detail.getAny().add(new InvalidParameterType("query", exception.getQuery()));
		detail.getAny().add(new InvalidParameterType("query", exception.getQuery()));
		fault.setDetail(detail);

		return Response.status(Response.Status.BAD_REQUEST).entity(fault).type(this.getMediaType()).build();
	}

	@XmlRootElement(name="invalidParameter")
	public static class InvalidParameterType {

		protected String name;
		protected String value;
		
		public InvalidParameterType() {
			// empty
		}
		
		public InvalidParameterType(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String value) {
			this.name = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
