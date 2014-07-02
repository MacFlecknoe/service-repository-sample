package com.healthmedia.ws.util;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;

/**
 * Container to load additional classes into JAXB context.
 * 
 * @author mlamber7
 *
 */
public class ClassArrayFactoryBean implements FactoryBean<Object> {

	private List<String> classNames;
	
	@Override
	public Object getObject() throws Exception {
		final Class<?>[] classes = new Class<?>[classNames.size()];
		for (int i = 0; i < classNames.size(); i++) {
			classes[i] = Class.forName(classNames.get(i));
		}
		return classes;
	}

	@Override
	public Class<?> getObjectType() {
		return Class[].class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public List<String> getClassNames() {
		return classNames;
	}

	public void setClassNames(List<String> classNames) {
		this.classNames = classNames;
	}
}