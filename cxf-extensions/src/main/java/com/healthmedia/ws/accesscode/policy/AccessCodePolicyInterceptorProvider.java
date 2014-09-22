package com.healthmedia.ws.accesscode.policy;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;

import com.healthmedia.ws.accesscode.AccessCodeInterceptor;

public class AccessCodePolicyInterceptorProvider extends AbstractPolicyInterceptorProvider {

	private static final long serialVersionUID = 1L;
	
	private static final Collection<QName> ASSERTION_TYPES = Arrays.asList(AccessCodeInterceptor.ACCESS_CODE_POLICY);
	
	public AccessCodePolicyInterceptorProvider() {
		super(ASSERTION_TYPES);
		getInInterceptors().add(new AccessCodeInterceptor(new AccessCodeInterceptor.AccessCodeContextProcessor()));
	}
}
