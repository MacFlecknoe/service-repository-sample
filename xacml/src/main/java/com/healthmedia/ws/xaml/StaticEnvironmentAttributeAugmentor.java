package com.healthmedia.ws.xaml;


import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.RequestType;

public class StaticEnvironmentAttributeAugmentor extends AbstractStaticAttributeAugmentor {
	
	public StaticEnvironmentAttributeAugmentor(List<AttributeType> attributes) {
		super(attributes);
	}

	@Override
	public RequestType process(RequestType xacmlRequest, List<AttributeType> attributes) {
		
		for(AttributeType attribute : attributes) {
			xacmlRequest.getEnvironment().getAttributes().add(attribute);
		}
		return xacmlRequest;
	}
}
