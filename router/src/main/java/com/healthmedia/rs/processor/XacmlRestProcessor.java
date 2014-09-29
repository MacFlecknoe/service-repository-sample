package com.healthmedia.rs.processor;

import java.util.Arrays;
import java.util.Date;

import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.helpers.XMLUtils;
import org.apache.cxf.rt.security.xacml.XACMLConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.security.xacml.core.JBossRequestContext;
import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.AttributeType;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.healthmedia.ws.xaml.ClasspathConfigurableJBossPDP;
import com.healthmedia.ws.xaml.PicketBoxXamlUtil;

/**
 * Creates a XACML request based upon passed REST headers and calls a configured PDP to determine if access should be allowed.
 * 
 * @author mlamber7
 *
 */
public class XacmlRestProcessor implements Processor {
	
	private static final Logger LOGGER = LogManager.getLogger(XacmlRestProcessor.class);
	
	private final PolicyDecisionPoint pdp;
	private final IXacmlRequestTransformer transformer;
	
	public XacmlRestProcessor(PolicyDecisionPoint pdp, IXacmlRequestTransformer transformer) {
		this.pdp = pdp;
		this.transformer = transformer;
	}
	
	public XacmlRestProcessor(PolicyDecisionPoint pdp) {
		this(pdp, new MockTokenXacmlTransformer());
	}
	
	public XacmlRestProcessor() {
		this(new ClasspathConfigurableJBossPDP(), new MockTokenXacmlTransformer());
	}
	
	public PolicyDecisionPoint getPolicyDecisionPoint() {
		return pdp;
	}
	
	private IXacmlRequestTransformer getXacmlRequestTransformer() {
		return transformer;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		RequestType requestType = getXacmlRequestTransformer().transform(exchange);
		
		RequestContext requestContext = new JBossRequestContext();
		requestContext.setRequest(requestType);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append("XACML request: ").append(XMLUtils.toString(requestContext.getDocumentElement())).toString());
		}
		//
		// hand the XACML request to the XACML decision engine
		//
		ResponseContext responseContext = getPolicyDecisionPoint().evaluate(requestContext);
		
		ResultType result = responseContext.getResult();
		
		if(!DecisionType.PERMIT.equals(result.getDecision())) {
			//
			// only allow explicit permits access
			//
			LOGGER.warn(result.getDecision().name());
			
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
			
			throw new CamelAuthorizationException("XACML Denial", exchange);
		}
	}
	
	public static interface IXacmlRequestTransformer {
		public RequestType transform(Exchange exchange);
	}
	
	/**
	 * TODO extract actual token information and set it to requesttype
	 * 
	 * @author mlamber7
	 *
	 */
	public static class SpringOauth2TokenTransformer implements IXacmlRequestTransformer {

		@Override
		public RequestType transform(Exchange exchange) {
			//
			// Authentication set in OAuth2AuthenticationProcessingFilter. Need to ensure camel route runs in same thread as oauth call. Otherwise
			// we can use the HttpRequest to carry the Authentication token (again see OAuth2AuthenticationProcessingFilter)
			//
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			//
			// now look up scope, client-id and resource-id and map them to XACML attributes
			//
			return null;
		}
	}
	
	public static class MockTokenXacmlTransformer implements IXacmlRequestTransformer {

		@Override
		public RequestType transform(Exchange exchange) {
			
			DateTime dt = new DateTime(new Date());

			AttributeType clientId = PicketBoxXamlUtil.createSimpleAttributeType("urn:oasis:names:tc:xacml:1.0:client:client-id", XACMLConstants.XS_STRING, "clientname");
			AttributeType scopeIds = PicketBoxXamlUtil.createSimpleAttributeListType("urn:oasis:names:tc:xacml:1.0:scope:scope-id", XACMLConstants.XS_STRING, Arrays.asList("read", "write"));
			//
			// There can be multiple subjects in a <Request> element. XACML context specifies that these subjects should be "disjunctive," which is defined in the standard as a sequence of 
			// predicates combined using the logical ‘OR’ operation. For example, one subject may represents the human user who initiates the request, another may represent the code that is 
			// doing the request. They are specified by subject categories.
			//
			// create the client subject
			//
			SubjectType clientSubjectType = new SubjectType();
			clientSubjectType.setSubjectCategory(XACMLConstants.SUBJECT_CAT_INTERMED_SUBJECT);
			clientSubjectType.getAttribute().add(clientId);
			clientSubjectType.getAttribute().add(scopeIds);

			AttributeType subjectId = PicketBoxXamlUtil.createSimpleAttributeType(XACMLConstants.SUBJECT_ID, XACMLConstants.XS_STRING, "username");
			//
			// create the resource owner subject: for one request, there must be at least one <Subject> having the “access-subject” category, 
			// which represents the direct accessing subject. 
			//
			SubjectType ownerSubjectType = new SubjectType();
			ownerSubjectType.setSubjectCategory(XACMLConstants.SUBJECT_CAT_ACCESS_SUBJECT);
			ownerSubjectType.getAttribute().add(subjectId);
			//
			// add attributes related to the requested resource
			//
			AttributeType resourceId = PicketBoxXamlUtil.createSimpleAttributeType(XACMLConstants.RESOURCE_ID, XACMLConstants.XS_STRING, exchange.getIn().getHeader("camelhttppath", String.class));
			
			ResourceType resourceType = new ResourceType();
			resourceType.getAttribute().add(resourceId);
			//
			// add attributes related to the type of action being performed
			//
			AttributeType actionId = PicketBoxXamlUtil.createSimpleAttributeType(XACMLConstants.ACTION_ID, XACMLConstants.XS_STRING, exchange.getIn().getHeader("camelhttpmethod", String.class));
			AttributeType accessCode = PicketBoxXamlUtil.createSimpleAttributeType("urn:healthmedia:names:action:access-code:v1", XACMLConstants.XS_STRING, exchange.getIn().getHeader("X-AccessCode", String.class));
			
			ActionType actionType = new ActionType();
			actionType.getAttribute().add(actionId);
			actionType.getAttribute().add(accessCode);
			//
			// add attributes related to the current environment (e.g. processor usage)
			//
			AttributeType currentDateTime = PicketBoxXamlUtil.createSimpleAttributeType(XACMLConstants.CURRENT_DATETIME, XACMLConstants.XS_DATETIME, dt.toString());
			
			EnvironmentType environmentType = new EnvironmentType();
			environmentType.getAttribute().add(currentDateTime);
			//
			// bundle the collected attributes together into a XACML request
			//
			RequestType requestType = new RequestType();
			
			requestType.getSubject().add(clientSubjectType);
			requestType.getSubject().add(ownerSubjectType);
			requestType.getResource().add(resourceType);
			requestType.setEnvironment(environmentType);
			requestType.setAction(actionType);
			
			return requestType;
		}
	}
}
