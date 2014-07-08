package com.healthmedia.ws.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.camel.component.cxf.common.header.MessageHeaderFilter;
import org.apache.camel.spi.HeaderFilterStrategy.Direction;
import org.apache.cxf.binding.soap.SoapBindingConstants;
import org.apache.cxf.binding.soap.SoapBindingFactory;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;

public class SoapHeaderFilter implements MessageHeaderFilter {

	private static final List<String> ACTIVATION_NS = Arrays.asList(
			SoapBindingConstants.SOAP11_BINDING_ID,
			SoapBindingFactory.SOAP_11_BINDING,
			SoapBindingFactory.SOAP_12_BINDING);

	private final Collection<QName> qnames;
	
	public SoapHeaderFilter(QName qname) {
		this(Collections.singletonList(qname));
	}
	
	public SoapHeaderFilter(Collection<QName> qnames) {
		this.qnames = qnames;
	}
	
	@Override
	public List<String> getActivationNamespaces() {
		return ACTIVATION_NS;
	}

	@Override
	public void filter(Direction direction, List<Header> headers) {
		
		if(headers != null) {
			Iterator<Header> iterator = headers.iterator();
			
			while (iterator.hasNext()) {
				Header header = iterator.next();
				
				if (!(header instanceof SoapHeader)) {
					continue;
				}
				SoapHeader soapHeader = SoapHeader.class.cast(header);
				if(qnames.contains(soapHeader.getName())) {
					iterator.remove();
				}
			}
		}
	}
}
