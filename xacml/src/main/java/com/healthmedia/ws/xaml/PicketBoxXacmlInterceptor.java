package com.healthmedia.ws.xaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.AbstractXACMLAuthorizingInterceptor;
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
import org.w3c.dom.Element;

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

	private final PolicyDecisionPoint pdp;
	private final List<IXacmlRequestPreprocessor> processors;
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp, List<IXacmlRequestPreprocessor> processors) {
		this.pdp = pdp;
		this.processors = processors;
	}
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp) {
		this(pdp, Arrays.asList((IXacmlRequestPreprocessor) new AccessCodeXacmlRequestAugmentor()));
	}
	
	public PicketBoxXacmlInterceptor() {
		this(new ClasspathConfigurableJBossPDP());
	}
	
	public List<IXacmlRequestPreprocessor> getRequestProcessors() {
		return processors;
	}
	
	@Override
	public ResponseType performRequest(RequestType xacmlRequest, Message message) throws Exception {
		
		RequestType processedXacmlRequest = preprocessRequest(xacmlRequest, message);
		
		RequestContext jbossXacmlRequest = new JBossRequestContext();
		jbossXacmlRequest.readRequest(processedXacmlRequest.getDOM());
		
		ResponseContext jbossXacmlResponse = pdp.evaluate(jbossXacmlRequest);
		ResponseType responseType = new XacmlResponseTransformer().transform(jbossXacmlResponse);
		
		return responseType;
	}
	
	private RequestType preprocessRequest(RequestType xacmlRequest, Message message) throws Exception {
		
		for(IXacmlRequestPreprocessor preprocessor : this.getRequestProcessors()) {
			xacmlRequest = preprocessor.process(xacmlRequest, message);
		}
		xacmlRequest.setDOM(OpenSAMLUtil.toDom(xacmlRequest, DOMUtils.createDocument()));
		
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
