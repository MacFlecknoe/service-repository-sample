package com.healthmedia.ws.common.error.v1;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.provider.ProviderFactory;
import org.apache.cxf.message.MessageImpl;

public class ExceptionMapperTest {
	
	@org.junit.Before
	public void setup() {
		ProviderFactory.getInstance().clearProviders();
	}
	
	@org.junit.Test
	public void testBadCustomExceptionMappersHierarchyWithGenerics() throws Exception {
		
		ProviderFactory pf = ProviderFactory.getInstance();
		
		BadExceptionMapperA exceptionMapperA = new BadExceptionMapperA();
		pf.registerUserProvider(exceptionMapperA);
		
		BadExceptionMapperB exceptionMapperB = new BadExceptionMapperB();
		pf.registerUserProvider(exceptionMapperB);
		
		Object mapperResponse1 = pf.createExceptionMapper(RuntimeExceptionA.class, new MessageImpl());
		assertSame(exceptionMapperA, mapperResponse1);
		
		Object mapperResponse2 = pf.createExceptionMapper(RuntimeExceptionB.class, new MessageImpl());
		assertSame(exceptionMapperB, mapperResponse2);
		
		Object mapperResponse3 = pf.createExceptionMapper(RuntimeExceptionAA.class, new MessageImpl());
		assertSame(exceptionMapperA, mapperResponse3);
		//
		// this will fail bcause of cxf bug
		//
		// Object mapperResponse4 = pf.createExceptionMapper(RuntimeExceptionBB.class, new MessageImpl());
		// assertSame(exceptionMapperB, mapperResponse4);
	}
	
	/**
	 * To fix the problem the mapper must NOT extend from a class that implements the ExceptionMapper interface. The parent class should be
	 * a normal java class and the mapper should implement the ExceptionMapper interface DIRECTLY!
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testFixedCustomExceptionMappersHierarchyWithGenerics() throws Exception {
		
		ProviderFactory pf = ProviderFactory.getInstance();
		
		FixedExceptionMapperA exceptionMapperA = new FixedExceptionMapperA();
		pf.registerUserProvider(exceptionMapperA);
		
		FixedExceptionMapperB exceptionMapperB = new FixedExceptionMapperB();
		pf.registerUserProvider(exceptionMapperB);
		
		Object mapperResponse1 = pf.createExceptionMapper(RuntimeExceptionA.class, new MessageImpl());
		assertSame(exceptionMapperA, mapperResponse1);
		
		Object mapperResponse2 = pf.createExceptionMapper(RuntimeExceptionB.class, new MessageImpl());
		assertSame(exceptionMapperB, mapperResponse2);
		
		Object mapperResponse3 = pf.createExceptionMapper(RuntimeExceptionAA.class, new MessageImpl());
		assertSame(exceptionMapperA, mapperResponse3);
		
		Object mapperResponse4 = pf.createExceptionMapper(RuntimeExceptionBB.class, new MessageImpl());
		assertSame(exceptionMapperB, mapperResponse4);
	}


	@org.junit.Test
	public void testGoodExceptionMappersHierarchyWithGenerics() throws Exception {
		
		ProviderFactory pf = ProviderFactory.getInstance();
		
		GoodRuntimeExceptionAMapper exceptionMapperA = new GoodRuntimeExceptionAMapper();
		pf.registerUserProvider(exceptionMapperA);
		
		GoodRuntimeExceptionBMapper exceptionMapperB = new GoodRuntimeExceptionBMapper();
		pf.registerUserProvider(exceptionMapperB);
		
		Object mapperResponse1 = pf.createExceptionMapper(RuntimeExceptionA.class, new MessageImpl());
		assertSame(exceptionMapperA, mapperResponse1);
		
		Object mapperResponse2 = pf.createExceptionMapper(RuntimeExceptionB.class, new MessageImpl());
		assertSame(exceptionMapperB, mapperResponse2);
		
		Object mapperResponse3 = pf.createExceptionMapper(RuntimeExceptionAA.class, new MessageImpl());
		assertSame(exceptionMapperA, mapperResponse3);
		
		Object mapperResponse4 = pf.createExceptionMapper(RuntimeExceptionBB.class, new MessageImpl());
		assertSame(exceptionMapperB, mapperResponse4);
	}
	
	private class RuntimeExceptionA extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	private class RuntimeExceptionAA extends RuntimeExceptionA {
		private static final long serialVersionUID = 1L;
	}
	
	private class RuntimeExceptionB extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	private class RuntimeExceptionBB extends RuntimeExceptionB {
		private static final long serialVersionUID = 1L;
	}
	
	private class GoodRuntimeExceptionAMapper implements ExceptionMapper<RuntimeExceptionA> {

		@Override
		public Response toResponse(RuntimeExceptionA exception) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private class GoodRuntimeExceptionBMapper implements ExceptionMapper<RuntimeExceptionB> {

		@Override
		public Response toResponse(RuntimeExceptionB exception) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	
	/**
	 * The parent implements ExceptionMapper and as a result will be what the ProviderFactory.Comparator class incorrectly chooses when sort ordering
	 * the mappers rather than its children
	 *
	 */
	private abstract class BadParentExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
	}
	
	/**
	 * This does not implement ExceptionMapper and so its parent will be incorrectly selected by he ProviderFactory.Comparator class for sort ordering
	 *
	 */
	@Provider
	private class BadExceptionMapperA extends BadParentExceptionMapper<RuntimeExceptionA> {

		@Override
		public Response toResponse(RuntimeExceptionA exception) {
			return null;
		}
	}
	
	/**
	 * This does not implement ExceptionMapper and so its parent will be incorrectly selected by he ProviderFactory.Comparator class for sort ordering
	 *
	 */
	@Provider
	private class BadExceptionMapperB extends BadParentExceptionMapper<RuntimeExceptionB> {

		@Override
		public Response toResponse(RuntimeExceptionB exception) {
			return null;
		}
	}
	
	
	/**
	 * This does NOT implement ExceptionMapper
	 *
	 */
	private abstract class FixedParentExceptionMapper {
	}
	
	/**
	 * The mapper implements ExceptionMapper directly
	 *
	 */
	@Provider
	private class FixedExceptionMapperA extends FixedParentExceptionMapper implements ExceptionMapper<RuntimeExceptionA> {

		@Override
		public Response toResponse(RuntimeExceptionA exception) {
			return null;
		}
	}
	
	/**
	 * The mapper implements ExceptionMapper directly
	 *
	 */
	@Provider
	private class FixedExceptionMapperB extends FixedParentExceptionMapper implements ExceptionMapper<RuntimeExceptionB> {

		@Override
		public Response toResponse(RuntimeExceptionB exception) {
			return null;
		}
	}
}
