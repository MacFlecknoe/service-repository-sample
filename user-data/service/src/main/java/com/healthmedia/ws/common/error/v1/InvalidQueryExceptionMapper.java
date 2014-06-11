package com.healthmedia.ws.common.error.v1;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.w3.soap.envelop.v1.Detail;
import org.w3.soap.envelop.v1.Fault;
import org.w3.soap.envelop.v1.Faultcode;
import org.w3.soap.envelop.v1.Faultreason;
import org.w3.soap.envelop.v1.Reasontext;
import org.w3.soap.envelop.v1.Subcode;

/**
 * Example exception mapper. Maps to a SOAP fault object defined in our xsd.
 * 
 */
@Provider
public class InvalidQueryExceptionMapper extends AbstractErrorV1ExceptionMapper<InvalidQueryException> {

	@Override
	public Response toResponse(InvalidQueryException exception) {

		Fault error = new Fault();
		//
		// the client has created the problem
		//
		Faultcode code = new Faultcode();
		code.setValue(new QName("", "Sender"));
		//
		// this is the actual error
		//
		Subcode subcode = new Subcode();
		subcode.setValue(new QName("", "BadArguments"));
		code.setSubcode(subcode);

		error.setCode(code);
		//
		// description of issue in human readable text
		//
		Faultreason reason = new Faultreason();
		Reasontext text = new Reasontext();
		text.setValue("Invalid Query Parameter");
		text.setLang(Locale.US.getLanguage());

		reason.getText().add(text);

		error.setReason(reason);
		//
		// application specific error message
		//
		Detail detail = new Detail();
		detail.getAny().add(new InvalidParameterType("query", exception.getQuery()));
		detail.getAny().add(new InvalidParameterType("query", exception.getQuery()));
		
		error.setDetail(detail);

		return Response.status(Response.Status.BAD_REQUEST).entity(error).type(this.getMediaType()).build();
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
