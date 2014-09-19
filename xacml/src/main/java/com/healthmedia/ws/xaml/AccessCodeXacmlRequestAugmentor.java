package com.healthmedia.ws.xaml;

import org.apache.cxf.message.Message;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.impl.AttributeTypeImplBuilder;
import org.opensaml.xacml.ctx.impl.AttributeValueTypeImplBuilder;

public class AccessCodeXacmlRequestAugmentor implements IXacmlRequestPreprocessor {

	@Override
	public RequestType process(RequestType xacmlRequest, Message message) {
		
		AttributeValueType attributeValueType = new AttributeValueTypeImplBuilder().buildObject();
		attributeValueType.setValue("testAccessCode");
		
		AttributeType attributeType = new AttributeTypeImplBuilder().buildObject();
		attributeType.setAttributeID("urn:healthmedia:xacml:action:v1:access-code");
		attributeType.setDataType("http://www.w3.org/2001/XMLSchema#string");
		attributeType.getAttributeValues().add(attributeValueType);
		
		xacmlRequest.getAction().getAttributes().add(attributeType);
		
		return xacmlRequest;
	}
}