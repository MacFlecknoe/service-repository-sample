package com.healthmedia.ws.common.error.v1;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public abstract class AbstractErrorV1ExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

	@Context
	private HttpHeaders headers;
	
	private static String XML_MEDIA_TYPE = "application/vnd.com.healthmedia.error+xml;version=1.0";
	private static String JSON_MEDIA_TYPE = "application/vnd.com.healthmedia.error+json;version=1.0";
	
	public String getMediaType() {
		
		for(MediaType type : headers.getAcceptableMediaTypes()) {
			
			if(type.getSubtype().contains("json")) {
				return JSON_MEDIA_TYPE;
			}
		}
		return XML_MEDIA_TYPE;
	}
}
