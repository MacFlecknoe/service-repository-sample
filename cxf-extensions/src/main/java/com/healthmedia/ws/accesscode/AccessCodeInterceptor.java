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

	public static final QName ACCESS_CODE_POLICY = new QName("urn:healthmedia:schema:common-policy:v1", "accessCodePolicy");
	private static final QName ACCESS_CODE_HEADER = new QName("urn:healthmedia:schema:common-header:v1", "accessCode");
	
	private final IAccessCodeValidator validator;
	private final XPathExpression accessCodeExpression;
	
	public AccessCodeInterceptor(IAccessCodeValidator validator) {
		super(Phase.PRE_INVOKE);
		try {
			MapNamespaceContext context = new MapNamespaceContext();
			context.addNamespace("ch", "urn:healthmedia:schema:common-header:v1");
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(context);
			
			this.accessCodeExpression = xpath.compile("//ch:accessCode/text()");
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
				validator.validate(accessCode);
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

	public static interface IAccessCodeValidator {
		
		/**
		 * Method should lookup and validate passed access code.
		 * 
		 * @param accessCode
		 * @throws SecurityException when access code is invalid
		 */
		public void validate(String accessCode) throws SecurityException;
	}
	
	public static class MockAccessCodeValidator implements IAccessCodeValidator {
		
		@Override
		public void validate(String accessCode) throws SecurityException {
			if(!"accessCode".equals(accessCode)) {
				throw new SecurityException("mock fault");
			}
		}
	}
}
