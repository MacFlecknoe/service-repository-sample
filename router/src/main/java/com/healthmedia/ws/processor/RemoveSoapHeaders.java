package com.healthmedia.ws.processor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.CastUtils;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;

/**
 * We need to remove the SOAP headers we have processed in the proxy.
 * 
 * @author mlamber7
 *
 */
public class RemoveSoapHeaders implements Processor {
	
	private final Collection<QName> qnames;
	
	public RemoveSoapHeaders(QName qname) {
		this(Collections.singletonList(qname));
	}
	
	public RemoveSoapHeaders(Collection<QName> qnames) {
		this.qnames = qnames;
	}
	
	public void process(Exchange exchange) throws Exception {
		
		List<SoapHeader> soapHeaders = CastUtils.cast((List<?>) exchange.getIn().getHeader(Header.HEADER_LIST)); // org.apache.cxf.headers.header.list
		
		for(SoapHeader header : soapHeaders) {
			if(qnames.contains(header.getName())){
				soapHeaders.remove(header);
			}
		}
	}
}
