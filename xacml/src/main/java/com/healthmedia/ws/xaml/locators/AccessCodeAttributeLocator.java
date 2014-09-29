package com.healthmedia.ws.xaml.locators;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeDesignator;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.attr.StringAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;

/**
 * Looks up the list of access-codes associated with a subject. Is a PIP within the XACML reference 
 * architecture. 
 * 
 * @author mlamber7
 *
 */
public class AccessCodeAttributeLocator extends AttributeLocator {

	private static final String ACCESS_CODE_IDENTIFIER = "urn:healthmedia:names:subject:access-code:v1";
	
	private final IAccessCodeFinder finder;
	
	public AccessCodeAttributeLocator(IAccessCodeFinder finder) {
		this.finder = finder;
	}
	
	public AccessCodeAttributeLocator() {
		this(new MockAccessCodeFinder());
	}
	
	@Override
	public boolean isDesignatorSupported() {
		// always return true, since this is a feature we always support
		return true;
	}

	@Override
	public Set<Integer> getSupportedDesignatorTypes() {
		//
		// return a single identifier that shows support for environment attrs
		//
		Set<Integer> set = new HashSet<Integer>();
		set.add(new Integer(AttributeDesignator.SUBJECT_TARGET));

		return set;
	}

	@Override
	public EvaluationResult findAttribute(
			URI attributeType, 
			URI attributeId,
			URI issuer, 
			URI subjectCategory, 
			EvaluationCtx context,
			int designatorType) {
		
		if (designatorType != AttributeDesignator.SUBJECT_TARGET) {
			// make sure this is an Subject attribute
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
		}
		if (!attributeId.toString().equals(ACCESS_CODE_IDENTIFIER)) {
			// make sure they're asking for our identifier
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
		}
		if (!attributeType.toString().equals(StringAttribute.identifier)) {
			// make sure they're asking for an string return value
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
		}
		// now that everything checks out, get the access-code list...
		Set<StringAttribute> accessCodeSet = new HashSet<StringAttribute>();
		
		for(String accessCode : finder.getAcccessCodes("test")) {
			accessCodeSet.add(new StringAttribute(accessCode));
		}
		return new EvaluationResult(new BagAttribute(attributeType, accessCodeSet));
	}
	
	public static interface IAccessCodeFinder {
		public Set<String> getAcccessCodes(String username);
	}
	
	public static class MockAccessCodeFinder implements IAccessCodeFinder {

		@Override
		public Set<String> getAcccessCodes(String username) {
			
			Set<String> accessCodeSet = new HashSet<String>();
			
			accessCodeSet.add("testAccessCode");
			accessCodeSet.add("anotherTestAccessCode");
			accessCodeSet.add("yetAnotherTestAccessCode");
			
			return accessCodeSet;
		}
	}
}
