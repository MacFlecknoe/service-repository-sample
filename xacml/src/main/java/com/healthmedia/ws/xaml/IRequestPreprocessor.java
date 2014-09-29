package com.healthmedia.ws.xaml;

import org.apache.cxf.message.Message;
import org.jboss.security.xacml.core.model.context.RequestType;

public interface IRequestPreprocessor {
	public RequestType process(RequestType xacmlRequest, Message message);
}