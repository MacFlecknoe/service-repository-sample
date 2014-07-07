package com.healthmedia.ws.i18n;

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

/**
 * <p>Adds support for Web Services Internationalization (WS-I18N). This implementation is meant to accept the specific locale and language preference(s) of the 
 * requester and act upon it. The default locale of the requester should be set to that of the international language node in the passed 
 * ws-i18n header.</p>
 * <p>As thread local locales are handled differently by different frameworks, setting the preferred locale is done via a configurable {@link ILocaleHandler}. 
 * Similarly. time zone preferences can also be managed via a {@link ITimeZoneHandler}.</p>
 * 
 * @see http://www.w3.org/TR/2012/NOTE-ws-i18n-20120522/
 * @author mlamber7
 *
 */
public class I18nHeaderInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	public static final QName WS_I18N_POLICY = new QName("http://www.w3.org/2008/04/ws-i18np", "i18n");
	private static final QName WS_I18N_HEADER = new QName("http://www.w3.org/2005/09/ws-i18n", "international");
	
	private final XPathExpression localeExpression;
	private final XPathExpression timeZoneExpression;
	
	private final ILocaleHandler localeHandler;
	private final ITimeZoneHandler timeZoneHandler;
	
	public I18nHeaderInterceptor(ILocaleHandler localeHandler, ITimeZoneHandler timeZoneHandler) {
		super(Phase.PRE_INVOKE);
		try {
			this.localeHandler = localeHandler;
			this.timeZoneHandler = timeZoneHandler;
			
			MapNamespaceContext context = new MapNamespaceContext();
			context.addNamespace("i18n", "http://www.w3.org/2005/09/ws-i18n");
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(context);
			
			this.localeExpression = xpath.compile("//i18n:international/i18n:locale/text()");
			this.timeZoneExpression = xpath.compile("//i18n:international/i18n:tz/text()");
			
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public I18nHeaderInterceptor(ILocaleHandler localeHandler) {
		this(localeHandler, new MockTimeZoneHandler());
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		try {
			Header header = message.getHeader(WS_I18N_HEADER);
			
			if(header != null) {
				
				Node node = (Node)header.getObject();
				
				String locale = (String) localeExpression.evaluate(node,  XPathConstants.STRING);
				String timeZone = (String) (String) timeZoneExpression.evaluate(node,  XPathConstants.STRING);
				
				localeHandler.setLocale(locale);
				timeZoneHandler.setTimeZone(timeZone);
				
				handleAssertion(message, true);
				
			} else {
				handleAssertion(message, false);
			}
		} catch (XPathExpressionException e) {
			throw new Fault(e);
		}
	}
	
	private void handleAssertion(SoapMessage message, boolean headerExists) {
		
		AssertionInfoMap aim =  message.get(AssertionInfoMap.class);
		Collection<AssertionInfo> ais = aim.get(WS_I18N_POLICY);
		
		if(ais != null) {
			for (AssertionInfo ai : ais) {
				Assertion assertion = ai.getAssertion();
				
				// if action is ignorable we should use default locale if a user preferred locale isnt passed
				if(assertion.isIgnorable() || assertion.isOptional() || headerExists) {
					ai.setAsserted(true);
				} else {
					ai.setAsserted(false);
				}
			}
		}
	}
	
	public static interface ILocaleHandler {
		public void setLocale(String i18nLocale);
	}
	
	public static interface ITimeZoneHandler {
		public void setTimeZone(String timeZone);
	}
	
	public static class MockTimeZoneHandler implements ITimeZoneHandler {

		@Override
		public void setTimeZone(String timeZone) {
			// do nothing
		}
	}
	
	public static class MockLocaleHandler implements ILocaleHandler {

		@Override
		public void setLocale(String i18nLocale) {
			// do nothing
		}
	}
}
