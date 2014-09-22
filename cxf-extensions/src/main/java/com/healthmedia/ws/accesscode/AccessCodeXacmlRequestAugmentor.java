package com.healthmedia.ws.accesscode;

import org.apache.cxf.message.Message;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.impl.AttributeTypeImplBuilder;
import org.opensaml.xacml.ctx.impl.AttributeValueTypeImplBuilder;

import com.healthmedia.ws.xaml.IXacmlRequestPreprocessor;

public class AccessCodeXacmlRequestAugmentor implements IXacmlRequestPreprocessor {

	@Override
	public RequestType process(RequestType xacmlRequest, Message message) {
		
		if(AccessCodeContext.getAccessCode() != null) {
			
			AttributeValueType attributeValueType = new AttributeValueTypeImplBuilder().buildObject();
			attributeValueType.setValue(AccessCodeContext.getAccessCode());
			
			AttributeType attributeType = new AttributeTypeImplBuilder().buildObject();
			attributeType.setAttributeID("urn:healthmedia:names:action:access-code:v1");
			attributeType.setDataType("http://www.w3.org/2001/XMLSchema#string");
			attributeType.getAttributeValues().add(attributeValueType);
			
			xacmlRequest.getAction().getAttributes().add(attributeType);
		}
		return xacmlRequest;
	}
}