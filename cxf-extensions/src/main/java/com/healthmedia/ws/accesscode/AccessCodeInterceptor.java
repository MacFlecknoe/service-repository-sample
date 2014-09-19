package com.healthmedia.ws.accesscode;

import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.MapNamespaceContext;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.apache.neethi.Assertion;
import org.w3c.dom.Node;

public class AccessCodeInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	public static final QName ACCESS_CODE_POLICY = new QName("urn:healthmedia:schema:policy:access-code:v1", "accessCodePolicy");
	private static final QName ACCESS_CODE_HEADER = new QName("urn:healthmedia:schema:header:access-code:v1", "accessCode");
	
	private final IAccessCodeProcessor validator;
	private final XPathExpression accessCodeExpression;
	
	public AccessCodeInterceptor(IAccessCodeProcessor validator) {
		super(Phase.PRE_INVOKE);
		try {
			MapNamespaceContext context = new MapNamespaceContext();
			context.addNamespace("ach", "urn:healthmedia:schema:header:access-code:v1");
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(context);
			
			this.accessCodeExpression = xpath.compile("//ach:accessCode/text()");
			this.validator = validator;
			
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		try {
			Header header = message.getHeader(ACCESS_CODE_HEADER);
			
			if(header != null) {
				
				Node node = (Node)header.getObject();
				
				String accessCode = (String) accessCodeExpression.evaluate(node,  XPathConstants.STRING);
				validator.process(accessCode);
				handleAssertion(message, true);
				
			} else {
				handleAssertion(message, false);
			}
		} catch (XPathExpressionException e) {
			throw new Fault(e);
		} catch (SecurityException e) {
			throw new Fault(e);
		}
	}
	
	private void handleAssertion(SoapMessage message, boolean headerExists) {
		
		AssertionInfoMap aim =  message.get(AssertionInfoMap.class);
		Collection<AssertionInfo> ais = aim.get(ACCESS_CODE_POLICY);
		
		if(ais != null) {
			for (AssertionInfo ai : ais) {
				Assertion assertion = ai.getAssertion();
				
				if(assertion.isIgnorable() || assertion.isOptional() || headerExists) {
					ai.setAsserted(true);
				} else {
					ai.setAsserted(false);
				}
			}
		}
	}

	public static interface IAccessCodeProcessor {
		
		/**
		 * Method should lookup and validate passed access code.
		 * 
		 * @param accessCode
		 * @throws SecurityException when access code is invalid
		 */
		public void process(String accessCode) throws SecurityException;
	}
	
	public static class MockAccessCodeValidator implements IAccessCodeProcessor {
		
		@Override
		public void process(String accessCode) throws SecurityException {
			if(!"accessCode".equals(accessCode)) {
				throw new SecurityException(new StringBuilder("Invalid access code: ").append(accessCode).toString());
			}
		}
	}
	
	public static class AccessCodeContextProcessor implements IAccessCodeProcessor {

		@Override
		public void process(String accessCode) throws SecurityException {
			AccessCodeContext.setAccessCode(accessCode);
		}
	}
}
