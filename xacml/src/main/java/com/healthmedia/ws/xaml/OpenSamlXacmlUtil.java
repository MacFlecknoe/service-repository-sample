package com.healthmedia.ws.xaml;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.util.DOM2Writer;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpenSamlXacmlUtil {
	
	public static String toString(XMLObject xmlObject) throws Exception {
		
		Document doc = DOMUtils.createDocument();
		Element element = OpenSAMLUtil.toDom(xmlObject, doc);
		
		return DOM2Writer.nodeToString(element);
	}
}