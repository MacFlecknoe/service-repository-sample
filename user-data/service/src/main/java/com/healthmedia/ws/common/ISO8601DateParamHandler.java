package com.healthmedia.ws.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cxf.jaxrs.ext.ParameterHandler;

public class ISO8601DateParamHandler implements ParameterHandler<Date> {
	
	@Override
	public Date fromString(String string) {
		
		SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			Date date =  dateFormat.parse(string);
			return date;
			
		} catch (ParseException e) {
			return  null ;
		}
	}
}
