package com.healthmedia.ws.filter;

import javax.xml.namespace.QName;

public class WsSecurityHeaderFilter extends SoapHeaderFilter {

	public WsSecurityHeaderFilter() {
		super(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"));
	}
}
