package com.healthmedia.ws.xaml;

import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.RequestType;

public class StaticSubjectAttributeAugmentor extends AbstractStaticAttributeAugmentor {
	
	public StaticSubjectAttributeAugmentor(List<AttributeType> attributes) {
		super(attributes);
	}

	@Override
	public RequestType process(RequestType xacmlRequest, List<AttributeType> attributes) {
		
		for(AttributeType attribute : attributes) {
			if(!xacmlRequest.getSubjects().isEmpty()) {
				xacmlRequest.getSubjects().iterator().next().getAttributes().add(attribute);
			}
		}
		return xacmlRequest;
	}
}
