package com.healthmedia.ws.xaml;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.security.xacml.core.model.context.AttributeType;
import org.jboss.security.xacml.core.model.context.AttributeValueType;

public class PicketBoxXamlUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(PicketBoxXamlUtil.class);
	
	public static AttributeType createSimpleAttributeType(String name, String dataType, String value) {
		
		if(LOGGER.isDebugEnabled()) {
			System.out.println("creating attribute:" + name + ", " + value);
		}
		AttributeValueType attributeValueType = new AttributeValueType();
		attributeValueType.getContent().add(value);
		
		AttributeType attributeType = new AttributeType();
		attributeType.setAttributeId(name);
		attributeType.setDataType(dataType);
		attributeType.getAttributeValue().add(attributeValueType);
		
		return attributeType;
	}
	
	public static AttributeType createSimpleAttributeListType(String name, String dataType, List<String> values) {
		
		AttributeType attributeType = new AttributeType();
		attributeType.setAttributeId(name);
		attributeType.setDataType(dataType);
		
		for(String value : values) {
			
			if(LOGGER.isDebugEnabled()) {
				System.out.println("creating attribute:" + name + ", " + value);
			}
			AttributeValueType attributeValueType = new AttributeValueType();
			attributeValueType.getContent().add(value);
			attributeType.getAttributeValue().add(attributeValueType);
		}
		return attributeType;
	}
}
