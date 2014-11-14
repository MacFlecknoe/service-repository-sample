package com.healthmedia.ws.xaml;

import org.apache.cxf.message.Message;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceContentType;
import org.w3c.dom.Node;

/**
 * Places SOAP request in the context of the XACML request for parsing/processing by PDP.
 * 
 * @author mlamber7
 *
 */
public class XacmlRequestContextAugmentor implements IRequestPreprocessor {

	@Override
	public RequestType process(RequestType xacmlRequest, Message message) {
		
		if(!xacmlRequest.getResource().isEmpty()) {
			//
			// add the SOAP message to the XACML request
			//
			Node soapMessage = message.getContent(Node.class);
			
			if(soapMessage != null) {
				ResourceContentType content = new ResourceContentType();
				content.getContent().add(soapMessage.getFirstChild());
				// grab the first resource
				xacmlRequest.getResource().iterator().next().setResourceContent(content);
			}
		}
		return xacmlRequest;
	}
}
