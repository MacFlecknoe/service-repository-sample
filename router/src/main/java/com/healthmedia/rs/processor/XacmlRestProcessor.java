package com.healthmedia.rs.processor;

import java.security.AccessControlException;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.rt.security.xacml.XACMLConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.security.xacml.core.JBossRequestContext;
import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.AttributeType;
import org.jboss.security.xacml.core.model.context.AttributeValueType;
import org.jboss.security.xacml.core.model.context.DecisionType;
import org.jboss.security.xacml.core.model.context.EnvironmentType;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceType;
import org.jboss.security.xacml.core.model.context.ResultType;
import org.jboss.security.xacml.core.model.context.SubjectType;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.joda.time.DateTime;

import com.healthmedia.ws.xaml.ClasspathConfigurableJBossPDP;

public class XacmlRestProcessor implements Processor {
	
	private static final Logger LOGGER = LogManager.getLogger(XacmlRestProcessor.class);
	
	private final PolicyDecisionPoint pdp;
	
	public XacmlRestProcessor(PolicyDecisionPoint pdp) {
		this.pdp = pdp;
	}
	
	public XacmlRestProcessor() {
		this(new ClasspathConfigurableJBossPDP());
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		DateTime dt = new DateTime(new Date());
		
		// set via separate processor
		AttributeType subjectId = createAttributeType(XACMLConstants.SUBJECT_ID, XACMLConstants.XS_STRING, "username");
		
		AttributeType resourceId = createAttributeType(XACMLConstants.RESOURCE_ID, XACMLConstants.XS_STRING, exchange.getIn().getHeader("camelhttppath", String.class));
		AttributeType actionId = createAttributeType(XACMLConstants.ACTION_ID, XACMLConstants.XS_STRING, exchange.getIn().getHeader("camelhttpmethod", String.class));
		AttributeType currentDateTime = createAttributeType(XACMLConstants.CURRENT_DATETIME, XACMLConstants.XS_DATETIME, dt.toString());
		
		// set via to separate processor
		AttributeType accessCode = createAttributeType("urn:healthmedia:names:action:access-code:v1", XACMLConstants.XS_STRING, "testAccessCode");
		
		SubjectType subjectType = new SubjectType();
		subjectType.getAttribute().add(subjectId);
		
		ResourceType resourceType = new ResourceType();
		resourceType.getAttribute().add(resourceId);
		
		ActionType actionType = new ActionType();
		actionType.getAttribute().add(actionId);
		actionType.getAttribute().add(accessCode);
		
		EnvironmentType environmentType = new EnvironmentType();
		environmentType.getAttribute().add(currentDateTime);
		
		RequestType requestType = new RequestType();
		requestType.getSubject().add(subjectType);
		requestType.getResource().add(resourceType);
		requestType.setEnvironment(environmentType);
		requestType.setAction(actionType);
		
		RequestContext xacmlRequest = new JBossRequestContext();
		xacmlRequest.setRequest(requestType);
		
		ResponseContext response = pdp.evaluate(xacmlRequest);
		
		ResultType result = response.getResult();
		
		if(!DecisionType.PERMIT.equals(result.getDecision())) {
			
			LOGGER.warn(result.getDecision().name()); // potentially log request and response
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
			
			throw new AccessControlException("Xacml has not permitted request");
		}
	}
	
	private AttributeType createAttributeType(String name, String dataType, String value) {
		
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
