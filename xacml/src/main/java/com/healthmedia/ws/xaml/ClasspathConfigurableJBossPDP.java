package com.healthmedia.ws.xaml;

import java.util.Set;

import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.jboss.security.xacml.interfaces.PolicyLocator;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.jboss.security.xacml.interfaces.XACMLPolicy;

public class ClasspathConfigurableJBossPDP implements PolicyDecisionPoint {

	private final PolicyDecisionPoint pdp;
	
	public ClasspathConfigurableJBossPDP(String configName) {
		this.pdp = new JBossPDP(Thread.currentThread().getContextClassLoader().getResourceAsStream(configName));
	}
	
	public ClasspathConfigurableJBossPDP() {
		this("pdp-config.xml");
	}
	
	@Override
	public void setPolicies(Set<XACMLPolicy> policies) {
		this.pdp.setPolicies(policies);
	}

	@Override
	public void setLocators(Set<PolicyLocator> locators) {
		this.pdp.setLocators(locators);
	}

	@Override
	public ResponseContext evaluate(RequestContext request) {
		return this.pdp.evaluate(request);
	}

}
