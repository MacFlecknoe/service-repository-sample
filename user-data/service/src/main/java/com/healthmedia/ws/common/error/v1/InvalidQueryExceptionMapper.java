package com.healthmedia.ws.common.error.v1;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.namespace.QName;

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
		subcode.setValue(new QName("", "BAD_QUERY"));
		code.setSubcode(subcode);
		
		error.setCode(code);
		//
		// description of issue in human readable text
		//
		Faultreason reason = new Faultreason();
		Reasontext text = new Reasontext();
		text.setValue("Bad query yo!");
		text.setLang("en-US");
		
		reason.getText().add(text);
		
		error.setReason(reason);
		
		return Response.status(Response.Status.BAD_REQUEST).entity(error).type(this.getMediaType()).build();
	}
}
