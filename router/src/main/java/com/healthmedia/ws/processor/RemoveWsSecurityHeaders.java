package com.healthmedia.ws.processor;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RemoveWsSecurityHeaders implements Processor {

	private RemoveCxfSoapHeaders processor = new RemoveCxfSoapHeaders(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"));
	
	@Override
	public void process(Exchange exchange) throws Exception {
		processor.process(exchange);
	}

}
