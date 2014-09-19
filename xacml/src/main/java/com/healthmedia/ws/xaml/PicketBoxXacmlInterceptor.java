package com.healthmedia.ws.xaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.AbstractXACMLAuthorizingInterceptor;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.util.DOM2Writer;
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
	private final List<IXacmlRequestProcessor> processors;
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp, List<IXacmlRequestProcessor> processors) {
		this.pdp = pdp;
		this.processors = processors;
	}
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp) {
		this(pdp, Arrays.asList((IXacmlRequestProcessor) new AccessCodeXacmlRequestAugmentor()));
	}
	
	public PicketBoxXacmlInterceptor() {
		this(new ClasspathConfigurableJBossPDP());
	}
	
	public List<IXacmlRequestProcessor> getXacmlRequestProcessors() {
		return processors;
	}
	
	@Override
	public ResponseType performRequest(RequestType xacmlRequest, Message message) throws Exception {
		
		RequestType processedXacmlRequest = processXacmlRequest(xacmlRequest, message);
		
		RequestContext jbossXacmlRequest = new JBossRequestContext();
		jbossXacmlRequest.readRequest(processedXacmlRequest.getDOM());
		
		ResponseContext jbossXacmlResponse = pdp.evaluate(jbossXacmlRequest);
		ResponseType responseType = new XacmlResponseTransformer().transform(jbossXacmlResponse);
		
		return responseType;
	}
	
	private RequestType processXacmlRequest(RequestType xacmlRequest, Message message) throws Exception {
		
		for(IXacmlRequestProcessor p : this.getXacmlRequestProcessors()) {
			xacmlRequest = p.process(xacmlRequest, message);
			System.out.println(OpenSamlXacmlUtil.toString(xacmlRequest));
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
	
	private static class OpenSamlXacmlUtil {
		
		public static String toString(XMLObject xmlObject) throws Exception {
			
			Document doc = DOMUtils.createDocument();
			Element element = OpenSAMLUtil.toDom(xmlObject, doc);
			
			return DOM2Writer.nodeToString(element);
		}
	}
}
