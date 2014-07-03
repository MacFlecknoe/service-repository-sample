package com.healthmedia.ws.assertion;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;

public class I18nPolicyInterceptorProvider extends AbstractPolicyInterceptorProvider {

	private static final long serialVersionUID = 1L;
	
	private static final Collection<QName> ASSERTION_TYPES = Arrays.asList(I18nHeaderInterceptor.ASSERTION);
	
	public I18nPolicyInterceptorProvider() {
		super(ASSERTION_TYPES);
		getInInterceptors().add(new I18nHeaderInterceptor(new SpringLocaleHandler()));
	}
}
