package com.healthmedia.rs.processor;

import java.security.Principal;

import javax.security.auth.Subject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityContextProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		//
		// Authentication set in OAuth2AuthenticationProcessingFilter
		//
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//
		// extract both the client-id, the resource-owner-id, the scopes and any other relevant REST attributes
		// and map them to XACML attributes
		//
		Object principle = authentication.getPrincipal();
		
		Subject subject = new Subject();
		
		exchange.getIn().setHeader(Exchange.AUTHENTICATION, subject);
	}
}
