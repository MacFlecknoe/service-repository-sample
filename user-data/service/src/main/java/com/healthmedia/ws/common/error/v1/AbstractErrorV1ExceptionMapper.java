package com.healthmedia.ws.common.error.v1;

public abstract class AbstractErrorV1ExceptionMapper<T extends Throwable> extends AbstractErrorExceptionMapper<T>{

	@Override
	public String getXmlMediaType() {
		return "application/vnd.com.healthmedia.error+xml;version=1.0";
	}

	@Override
	public String getJsonMediaType() {
		return "application/vnd.com.healthmedia.error+json;version=1.0";
	}
}
