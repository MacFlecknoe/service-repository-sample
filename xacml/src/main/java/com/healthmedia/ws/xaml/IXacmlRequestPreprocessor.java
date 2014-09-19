package com.healthmedia.ws.xaml;

import org.apache.cxf.message.Message;
import org.opensaml.xacml.ctx.RequestType;

public interface IXacmlRequestPreprocessor {
	public RequestType process(RequestType xacmlRequest, Message message);
}