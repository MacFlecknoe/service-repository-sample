package com.healthmedia.ws.xaml;

import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.RequestType;

public class StaticResourceAttributeAugmentor extends AbstractStaticAttributeAugmentor {
	
	public StaticResourceAttributeAugmentor(List<AttributeType> attributes) {
		super(attributes);
	}

	@Override
	public RequestType process(RequestType xacmlRequest, List<AttributeType> attributes) {
		
		for(AttributeType attribute : attributes) {
			if(!xacmlRequest.getResources().isEmpty()) {
				xacmlRequest.getResources().iterator().next().getAttributes().add(attribute);
			}
		}
		return xacmlRequest;
	}
}
