package com.healthmedia.ws.xaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathConstants;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.helpers.XMLUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.AbstractXACMLAuthorizingInterceptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.security.xacml.core.JBossRequestContext;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceContentType;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.opensaml.xacml.ctx.impl.ResponseTypeUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	private XacmlRequestTransformer requestTransformer;
	private XacmlResponseTransformer responseTransformer;
	
	public PicketBoxXacmlInterceptor(PolicyDecisionPoint pdp, List<IRequestPreprocessor> processors){
		this.pdp = pdp;
		this.processors = processors;
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
	
	public PolicyDecisionPoint getPolicyDecisionPoint() {
		return this.pdp;
	}
	
	private XacmlRequestTransformer getXacmlRequestTransformer() {
		if(this.requestTransformer == null) {
			this.requestTransformer = new XacmlRequestTransformer();
		}
		return this.requestTransformer;
	}
	
	private XacmlResponseTransformer getXacmlResponseTransformer() {
		if(this.responseTransformer == null) {
			this.responseTransformer = new XacmlResponseTransformer();
		}
		return this.responseTransformer;
	}
	
	@Override
	public org.opensaml.xacml.ctx.ResponseType performRequest(org.opensaml.xacml.ctx.RequestType opensamlRequest, Message message) throws Exception {
		//
		// Transforms an Opensaml XACML request to a PicketBox XACML request
		//
		RequestType requestType = getXacmlRequestTransformer().transform(opensamlRequest);
		//
		// move to preprocessor... should just peel off the access code that is being used... use schema:user:accessCode as key
		//
		if(!requestType.getResource().isEmpty()) {
			//
			// add the SOAP message to the XACML request
			//
			ResourceContentType content = new ResourceContentType();
			
			Node soapMessage = message.getContent(Node.class);
			
			javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
			javax.xml.xpath.XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(new NamespaceContext() {

				@Override
				public Iterator getPrefixes(String arg0) {
					return null;
				}

				@Override
				public String getPrefix(String arg0) {
					return null;
				}

				@Override
				public String getNamespaceURI(String arg0) {
					if("soap".equals(arg0)) {
						return "http://www.w3.org/2003/05/soap-envelope";
					} else if("user".equals(arg0)) {
						return "urn:healthmedia:schema:user:v1";
					} else if("userService".equals(arg0)) {
						return "urn:healthmedia:wsdl:user:v1";
					}
					return null;
				}
			});
			javax.xml.xpath.XPathExpression expression = xpath.compile("//user:user/user:accessCode/text()");
			
			NodeList accessCodes = (NodeList) expression.evaluate(soapMessage, XPathConstants.NODESET);
			
			content.getContent().add(soapMessage.getFirstChild());
			
			requestType.getResource().get(0).setResourceContent(content);
		}
		//
		// Augment the CXF configured attributes with our own custom attributes
		//
		requestType = preprocessRequest(requestType, message);
		
		JBossRequestContext requestContext = new JBossRequestContext();
		requestContext.setRequest(requestType);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder().append("XACML request: ").append(XMLUtils.toString(requestContext.getDocumentElement())).toString());
		}
		//
		// call the PicketBox Policy Decision Point
		//
		ResponseContext responseContext = getPolicyDecisionPoint().evaluate(requestContext);
		//
		// Transforms the PicketBox response back to an Opensaml response
		//
		org.opensaml.xacml.ctx.ResponseType responseType = getXacmlResponseTransformer().transform(responseContext);
		
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
	
	/**
	 * Transforms Opensaml response objects to Picketbox response objects and vice versa.
	 * 
	 * @author mlamber7
	 *
	 */
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
	
	/**
	 * Transforms Opensaml request objects to Picketbox request objects and vice versa.
	 * 
	 * @author mlamber7
	 *
	 */
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
}
