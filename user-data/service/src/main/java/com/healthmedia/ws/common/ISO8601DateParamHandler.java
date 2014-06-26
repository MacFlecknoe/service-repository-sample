package com.healthmedia.ws.common;

import java.util.Date;

import org.apache.cxf.jaxrs.ext.ParameterHandler;
import org.joda.time.DateTime;

public class ISO8601DateParamHandler implements ParameterHandler<Date> {
	
	@Override
	public Date fromString(String string) {
		try {
			return new DateTime(string).toDate();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
