package com.healthmedia.ws.xaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.AbstractXACMLAuthorizingInterceptor;
import org.jboss.security.xacml.core.JBossPDP;
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

	private final PolicyDecisionPoint pdp;
	private final XacmlResponseTransformer transformer; 
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp) {
		this.pdp = pdp;
		this.transformer = new XacmlResponseTransformer();
	}
	
	public PicketBoxXacmlInterceptor(String configName) {
		this(new JBossPDP(Thread.currentThread().getContextClassLoader().getResourceAsStream(configName)));
	}
	
	public PicketBoxXacmlInterceptor() {
		this("pdp-config.xml");
	}
	
	@Override
	public ResponseType performRequest(RequestType xacmlRequest, Message message) throws Exception {
		
		RequestContext jbossXacmlRequest = new JBossRequestContext();
		jbossXacmlRequest.readRequest(xacmlRequest.getDOM());
		
		ResponseContext jbossXacmlResponse = pdp.evaluate(jbossXacmlRequest);
		ResponseType responseType = transformer.transform(jbossXacmlResponse);
		
		return responseType;
	}
	
	private static class XacmlResponseTransformer {
		
		private final DocumentBuilderFactory factory;
		
		public XacmlResponseTransformer() {
			this.factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
		}
		
		private synchronized DocumentBuilder getDocumentBuilder() throws Exception {
			return factory.newDocumentBuilder();
		}
		
		public ResponseType transform(ResponseContext responseCtx) throws Exception {
			//
			// Serialize to standard XACML XML format and leverage API marshallers to convert to/from each frameworks native objects. 
			// This is expensive but the reduction in complexity is worth the nominal overhead.
			// 
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			responseCtx.marshall(outStream);
			
			Document doc = getDocumentBuilder().parse(new ByteArrayInputStream(outStream.toByteArray()));
			
			XMLObject responseType = new ResponseTypeUnmarshaller().unmarshall(doc.getDocumentElement());
			
			return (ResponseType) responseType;
		}
	}
}
