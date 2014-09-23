package com.healthmedia.ws.xaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.AbstractXACMLAuthorizingInterceptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.jboss.security.xacml.core.JBossRequestContext;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.ctx.impl.ResponseTypeUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Document;

/**
 * The PEP in the XACML reference architecture. Forwards requests to a configured PDP point.
 * 
 * https://developer.jboss.org/wiki/PicketBoxXACMLSimpleWalkThrough
 * http://anonsvn.jboss.org/repos/jbossas/projects/security/security-xacml/tags/2.0.6.Final/jboss-xacml/src/test/java/org/jboss/test/security/test/xacml/XACMLUnitTestCase.java
 *
 * @author mlamber7
 *
 */
public class PicketBoxXacmlInterceptor extends AbstractXACMLAuthorizingInterceptor {
	
	private static final Logger LOGGER = LogManager.getLogger(PicketBoxXacmlInterceptor.class);

	private final PolicyDecisionPoint pdp;
	private final List<IRequestPreprocessor> processors;
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp, List<IRequestPreprocessor> processors) {
		this.pdp = pdp;
		this.processors = processors;
		this.addAfter("com.healthmedia.ws.accesscode.AccessCodeInterceptor");
	}
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp) {
		this(pdp, new ArrayList<IRequestPreprocessor>());
	}
	
	public PicketBoxXacmlInterceptor(List<IRequestPreprocessor> processors) {
		this(new ClasspathConfigurableJBossPDP(), processors);
	}
	
	public List<IRequestPreprocessor> getRequestProcessors() {
		return processors;
	}
	
	@Override
	public ResponseType performRequest(RequestType xacmlRequest, Message message) throws Exception {
		
		RequestType processedXacmlRequest = preprocessRequest(xacmlRequest, message);
		
		RequestContext jbossXacmlRequest = new JBossRequestContext();
		jbossXacmlRequest.readRequest(processedXacmlRequest.getDOM());
		
		ResponseContext jbossXacmlResponse = pdp.evaluate(jbossXacmlRequest);
		ResponseType responseType = new XacmlResponseTransformer().transform(jbossXacmlResponse);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append("XACML response: ").append(OpenSamlXacmlUtil.toString(responseType)).toString());
		}
		return responseType;
	}
	
	private RequestType preprocessRequest(RequestType xacmlRequest, Message message) throws Exception {
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append("pre-processed XACML request: ").append(OpenSamlXacmlUtil.toString(xacmlRequest)).toString());
		}
		for(IRequestPreprocessor preprocessor : this.getRequestProcessors()) {
			xacmlRequest = preprocessor.process(xacmlRequest, message);
		}
		if(xacmlRequest.getDOM() == null) {
			// changing the XACML request has invalidated its DOM; we need to regenerate it
			xacmlRequest.setDOM(OpenSAMLUtil.toDom(xacmlRequest, DOMUtils.createDocument()));
		}
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append("post-processed XACML request: ").append(OpenSamlXacmlUtil.toString(xacmlRequest)).toString());
		}
		return xacmlRequest;
	}
	
	private static class XacmlResponseTransformer {
		
		public XacmlResponseTransformer() {
		}
		
		public ResponseType transform(ResponseContext responseCtx) throws Exception {
			//
			// Serialize to standard XACML XML format and leverage API marshallers to convert to/from each frameworks native objects. 
			// This is expensive but the reduction in complexity is worth the nominal overhead.
			// 
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			responseCtx.marshall(outStream);
			Document doc = DOMUtils.readXml(new ByteArrayInputStream(outStream.toByteArray()));
			
			XMLObject responseType = new ResponseTypeUnmarshaller().unmarshall(doc.getDocumentElement());
			
			return (ResponseType) responseType;
		}
	}
}
