package com.healthmedia.ws.filter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;

public class SampleFilter implements RequestHandler {

	@Context
	private HttpServletRequest httpRequest;
	
	@Override
	public Response handleRequest(Message message, ClassResourceInfo resourceClass) {
		// does nothing
		return null;
	}
}
