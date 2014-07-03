package com.healthmedia.ws.assertion;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.ServiceModelUtil;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.apache.neethi.Assertion;
import org.w3._2005._09.ws_i18n.International;
import org.w3c.dom.Node;

/**
 * <p>Adds support for Web Services Internationalization (WS-I18N). This implementation is meant to accept the specific locale and language preference(s) of the 
 * requester and act upon it. In this specific case the default locale of the requester is set to that of the international language node in the passed 
 * ws-i18n header.</p>
 * 
 * <p>In order for this interceptor to work it must be able to unmarshall ws-i18n elements; for this to happen {@link org.w3._2005._09.ws_i18n.International} needs to be 
 * included in the server's JAXB context.</p>
 * 
 * @see http://www.w3.org/TR/2012/NOTE-ws-i18n-20120522/
 * @author mlamber7
 *
 */
public class I18nHeaderInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	public static final QName WS_I18N_POLICY = new QName("http://www.w3.org/2008/04/ws-i18np", "i18n");
	private static final QName WS_I18N_HEADER = new QName("http://www.w3.org/2005/09/ws-i18n", "international");
	
	private final ILocaleHandler handler;
	
	public I18nHeaderInterceptor(ILocaleHandler handler) {
		super(Phase.PRE_INVOKE);
		this.handler = handler;
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		
		Header header = message.getHeader(WS_I18N_HEADER);
		
		if(header != null) {
			
			Service service = ServiceModelUtil.getService(message.getExchange());
			DataReader<Node> dataReader = service.getDataBinding().createReader(Node.class);
			
			International i18nHeader = (International)dataReader.read(WS_I18N_HEADER, (Node)header.getObject(), International.class);
			message.setContextualProperty(International.class.getName(), i18nHeader);

			handler.setLocale(i18nHeader);
			
			handleAssertion(message, true);
		} else {
			handleAssertion(message, false);
		}
	}
	
	private void handleAssertion(SoapMessage message, boolean headerExists) {
		
		AssertionInfoMap aim =  message.get(org.apache.cxf.ws.policy.AssertionInfoMap.class);
		Collection<AssertionInfo> ais = aim.get(WS_I18N_POLICY);
		
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
	
	public static interface ILocaleHandler {
		public void setLocale(International i18nLocale);
	}
}
