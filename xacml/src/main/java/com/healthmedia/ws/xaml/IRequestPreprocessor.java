package com.healthmedia.ws.xaml;

import org.apache.cxf.message.Message;
import org.opensaml.xacml.ctx.RequestType;

public interface IRequestPreprocessor {
	public RequestType process(RequestType xacmlRequest, Message message);
}