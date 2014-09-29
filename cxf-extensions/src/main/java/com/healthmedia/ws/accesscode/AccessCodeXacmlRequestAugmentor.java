package com.healthmedia.ws.accesscode;

import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.XACMLConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.AttributeType;
import org.jboss.security.xacml.core.model.context.AttributeValueType;
import org.jboss.security.xacml.core.model.context.RequestType;

import com.healthmedia.ws.xaml.IRequestPreprocessor;

/**
 * Adds the access code associated with the present request to the XAML request context.
 * 
 * @author mlamber7
 *
 */
public class AccessCodeXacmlRequestAugmentor implements IRequestPreprocessor {

	private static final Logger LOGGER = LogManager.getLogger(AccessCodeXacmlRequestAugmentor.class);
	
	private static final String ACCESS_CODE_ACTION_NAME = "urn:healthmedia:names:action:access-code:v1";
	
	@Override
	public RequestType process(RequestType xacmlRequest, Message message) {
		
		if(AccessCodeContext.getAccessCode() != null) {
			
			AttributeType accessCode = PicketlinkXamlUtil.createSimpleAttributeType(ACCESS_CODE_ACTION_NAME, XACMLConstants.XS_STRING, AccessCodeContext.getAccessCode());
			
			if(xacmlRequest.getAction() == null) {
				xacmlRequest.setAction(new ActionType());
			}
			xacmlRequest.getAction().getAttribute().add(accessCode);
		}
		return xacmlRequest;
	}
	
	private static class PicketlinkXamlUtil {
		
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
	}
}