package com.healthmedia.ws.assertion;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;

public class CustomPolicyInterceptorProvider extends AbstractPolicyInterceptorProvider {

	private static final long serialVersionUID = 1L;
	
	private static final Collection<QName> ASSERTION_TYPES = Arrays.asList(I18nHeaderInterceptor.ASSERTION);
	
	public CustomPolicyInterceptorProvider(Collection<QName> at) {
		super(at);
	}
	
	public CustomPolicyInterceptorProvider() {
		super(ASSERTION_TYPES);
		getInInterceptors().add(new SpringI18nHeaderInterceptor());
	}

}
