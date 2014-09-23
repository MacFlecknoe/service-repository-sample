package com.healthmedia.ws.xaml;

import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.apache.cxf.message.Message;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.impl.AttributeTypeImplBuilder;
import org.opensaml.xacml.ctx.impl.AttributeValueTypeImplBuilder;

/**
 * Mechanism to supply static XACML attributes to all XACML requests flowing through the PicketBoxXacmlInterceptor.
 * 
 * @author mlamber7
 *
 */
public abstract class AbstractStaticAttributeAugmentor implements IRequestPreprocessor {

	private final List<AttributeType> attributes;
	
	public AbstractStaticAttributeAugmentor(List<AttributeType> attributes) {
		this.attributes = attributes;
	}
	
	public List<AttributeType> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String attributeId, String dataType, String value) {
		
		AttributeValueType attributeValueType = new AttributeValueTypeImplBuilder().buildObject();
		attributeValueType.setValue(value);
		
		AttributeType attributeType = new AttributeTypeImplBuilder().buildObject();
		attributeType.setAttributeID(attributeId);
		attributeType.setDataType(dataType);
		attributeType.getAttributeValues().add(attributeValueType);
		
		this.getAttributes().add(attributeType);
	}
	
	public void addAttribute(String attributeId, String value) {
		this.addAttribute(attributeId, "http://www.w3.org/2001/XMLSchema#string", value);
	}
	
	@Override
	public RequestType process(RequestType xacmlRequest, Message message) {
		return this.process(xacmlRequest, attributes);
	}
	
	/**
	 * Add attributes to XACML Request.
	 * 
	 * @param xacmlRequest
	 * @param attributes
	 * @return
	 */
	public abstract RequestType process(RequestType xacmlRequest, List<AttributeType> attributes);
}
