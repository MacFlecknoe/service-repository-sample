package com.healthmedia.ws.xaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.AbstractXACMLAuthorizingInterceptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.util.DOM2Writer;
import org.jboss.security.xacml.core.JBossRequestContext;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.opensaml.xacml.ctx.impl.ResponseTypeUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The PEP in the XACML reference architecture. Forwards requests to a configured PDP point. This class marries the OpenSAML libraries (which supplies the PEP
 * interface leveraged by CXF) with JBoss's PicketBox libraries (which supplies the PDP interface).
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
	
	private final XacmlRequestTransformer requestTransformer;
	private final XacmlResponseTransformer responseTransformer;
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp, List<IRequestPreprocessor> processors){
		this.pdp = pdp;
		this.processors = processors;
		this.requestTransformer = new XacmlRequestTransformer();
		this.responseTransformer = new XacmlResponseTransformer();
		this.addAfter("com.healthmedia.ws.accesscode.AccessCodeInterceptor"); // make sure this executes after access code is handled
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
	/**
	 * Transforms an Opensaml XACML request to a Picketlink XACML request, calls the configured Picketlink PDP and transforms the Picketlink 
	 * response to an Opensaml response which is returned to the caller
	 */
	public org.opensaml.xacml.ctx.ResponseType performRequest(org.opensaml.xacml.ctx.RequestType opensamlRequest, Message message) throws Exception {

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append("XACML request: ").append(OpenSamlXacmlUtil.toString(opensamlRequest)).toString());
		}
		RequestType requestType = requestTransformer.transform(opensamlRequest);
		requestType = preprocessRequest(requestType, message);
		
		JBossRequestContext requestContext = new JBossRequestContext();
		requestContext.setRequest(requestType);
		
		ResponseContext responseContext = pdp.evaluate(requestContext);
		
		org.opensaml.xacml.ctx.ResponseType responseType = responseTransformer.transform(responseContext);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append("XACML response: ").append(OpenSamlXacmlUtil.toString(responseType)).toString());
		}
		return responseType;
	}
	
	/**
	 * Augment request with additional attributes.
	 */
	private org.jboss.security.xacml.core.model.context.RequestType preprocessRequest(org.jboss.security.xacml.core.model.context.RequestType xacmlRequest, Message message) throws Exception {
		
		for(IRequestPreprocessor preprocessor : this.getRequestProcessors()) {
			xacmlRequest = preprocessor.process(xacmlRequest, message);
		}
		return xacmlRequest;
	}
	
	private static class XacmlResponseTransformer {
		
		public XacmlResponseTransformer() {
		}
		
		public org.opensaml.xacml.ctx.ResponseType transform(ResponseContext responseContext) throws Exception {
			//
			// Serialize to standard XACML XML format and leverage API marshallers to convert to/from each frameworks native objects. 
			// This is expensive but the reduction in complexity is worth the nominal overhead.
			// 
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			responseContext.marshall(outStream);
			Document doc = DOMUtils.readXml(new ByteArrayInputStream(outStream.toByteArray()));
			
			XMLObject responseType = new ResponseTypeUnmarshaller().unmarshall(doc.getDocumentElement());
			
			return (org.opensaml.xacml.ctx.ResponseType) responseType;
		}
	}
	
	private static class XacmlRequestTransformer {
		
		private JAXBContext requestTypeContext;
		
		public XacmlRequestTransformer() {
			try {
				this.requestTypeContext = JAXBContext.newInstance(RequestType.class);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public RequestType transform(org.opensaml.xacml.ctx.RequestType opensamlRequest) {
			try {
				Unmarshaller unmarshaller = requestTypeContext.createUnmarshaller();
				
				JAXBElement<RequestType> root = unmarshaller.unmarshal(opensamlRequest.getDOM(), RequestType.class);
				RequestType request = root.getValue();
				
				return request;
				
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
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
