package com.healthmedia.ws.processor;

import java.util.ArrayList;
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
public class RemoveCxfSoapHeaders implements Processor {
	
	private final Collection<QName> qnames;
	
	public RemoveCxfSoapHeaders(QName qname) {
		this(Collections.singletonList(qname));
	}
	
	public RemoveCxfSoapHeaders(Collection<QName> qnames) {
		this.qnames = qnames;
	}
	
	public void process(Exchange exchange) throws Exception {
		
		List<SoapHeader> soapHeaders = new ArrayList<SoapHeader>();
		// retrieve all cxf headers: org.apache.cxf.headers.header.list
		List<SoapHeader> currentSoapHeaders = CastUtils.cast((List<?>) exchange.getIn().getHeader(Header.HEADER_LIST)); 
		
		for(SoapHeader header : currentSoapHeaders) {
			// reinsert all headers except those that match our configured qnames
			if(!qnames.contains(header.getName())) {
				soapHeaders.add(header);
			}
		}
		exchange.getIn().setHeader(Header.HEADER_LIST, soapHeaders);
	}
}
