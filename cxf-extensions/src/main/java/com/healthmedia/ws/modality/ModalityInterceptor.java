package com.healthmedia.ws.modality;

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

public class ModalityInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	public static final QName MODALITY_POLICY = new QName("urn:healthmedia:schema:policy:modality:v1", "modalityPolicy");
	private static final QName MODALITY_HEADER = new QName("urn:healthmedia:schema:header:modality:v1", "modality");
	
	private final IModalityHandler handler;
	private final XPathExpression modalityExpression;
	
	public ModalityInterceptor(IModalityHandler handler) {
		super(Phase.PRE_INVOKE);
		try {
			MapNamespaceContext context = new MapNamespaceContext();
			context.addNamespace("mh", "urn:healthmedia:schema:header:modality:v1");
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(context);
			
			this.modalityExpression = xpath.compile("//mh:modality/text()");
			this.handler = handler;
			
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		try {
			Header header = message.getHeader(MODALITY_HEADER);
			
			if(header != null) {
				
				Node node = (Node)header.getObject();
				
				String accessCode = (String) modalityExpression.evaluate(node, XPathConstants.STRING);
				handler.handle(accessCode);
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
		Collection<AssertionInfo> ais = aim.get(MODALITY_POLICY);
		
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

	public static interface IModalityHandler {
		
		/**
		 * Method should lookup and validate passed access code.
		 * 
		 * @param accessCode
		 * @throws SecurityException when access code is invalid
		 */
		public void handle(String modality);
	}
	
	public static class MockModalityHandler implements IModalityHandler {

		@Override
		public void handle(String modality) {
			ModalityContextContainer.getInstance().setModality(modality);
		}
	}
}
