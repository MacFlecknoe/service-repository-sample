package com.healthmedia.ws.common.error.v1;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Due to a bug in CXF's {@link org.apache.cxf.jaxrs.provider.ProviderFactory}, mappers which extend this class must ALSO implement ExceptionMapper
 *
 * @see com.healthmedia.ws.common.error.v1.ExceptionMapperTest
 */
public abstract class AbstractErrorExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
	
	@Context
	private HttpHeaders headers;
	
	public String getMediaType() {
		for(MediaType type : headers.getAcceptableMediaTypes()) {
			if(type.getSubtype().contains("json")) {
				return getJsonMediaType();
			}
		}
		return getXmlMediaType();
	}

	public abstract String getXmlMediaType();

	public abstract String getJsonMediaType();
}
