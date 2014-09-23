package com.healthmedia.ws.xaml;


import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.RequestType;

public class StaticActionAttributeAugmentor extends AbstractStaticAttributeAugmentor {
	
	public StaticActionAttributeAugmentor(List<AttributeType> attributes) {
		super(attributes);
	}

	@Override
	public RequestType process(RequestType xacmlRequest, List<AttributeType> attributes) {
		
		for(AttributeType attribute : attributes) {
			xacmlRequest.getAction().getAttributes().add(attribute);
		}
		return xacmlRequest;
	}
}
