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
 * Looks up the global user id associated with a subject. Is a PIP within the XACML reference 
 * architecture. 
 * 
 * @author mlamber7
 *
 */
public class GlobalUserIdAttributeLocator extends AttributeLocator {

	private static final String GLOBAL_USER_ID_IDENTIFIER = "urn:healthmedia:names:1.0:subject:global-user-id";
	
	private final IGlobalUserIdFinder finder;
	
	public GlobalUserIdAttributeLocator(IGlobalUserIdFinder finder) {
		this.finder = finder;
	}
	
	public GlobalUserIdAttributeLocator() {
		this(new MockGlobalUserIdFinder());
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
		if (!attributeId.toString().equals(GLOBAL_USER_ID_IDENTIFIER)) {
			// make sure they're asking for our identifier
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
		}
		if (!attributeType.toString().equals(StringAttribute.identifier)) {
			// make sure they're asking for an string return value
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
		}
		Set<StringAttribute> globalUserIdSet = new HashSet<StringAttribute>();
		globalUserIdSet.add(new StringAttribute(finder.findGlobalUserId("test")));
		
		return new EvaluationResult(new BagAttribute(attributeType, globalUserIdSet));
	}
	
	public static interface IGlobalUserIdFinder {
		public String findGlobalUserId(String username);
	}
	
	public static class MockGlobalUserIdFinder implements IGlobalUserIdFinder {

		@Override
		public String findGlobalUserId(String username) {
			return "14";
		}
	}
}
