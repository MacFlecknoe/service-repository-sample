package com.healthmedia.ws.i18n.policy;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;

import com.healthmedia.ws.i18n.I18nHeaderInterceptor;
import com.healthmedia.ws.i18n.SpringLocaleHandler;

public class I18nPolicyInterceptorProvider extends AbstractPolicyInterceptorProvider {

	private static final long serialVersionUID = 1L;
	
	private static final Collection<QName> ASSERTION_TYPES = Arrays.asList(I18nHeaderInterceptor.WS_I18N_POLICY);
	
	public I18nPolicyInterceptorProvider() {
		super(ASSERTION_TYPES);
		getInInterceptors().add(new I18nHeaderInterceptor(new SpringLocaleHandler()));
	}
}
