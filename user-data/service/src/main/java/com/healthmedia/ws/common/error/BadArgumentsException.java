package com.healthmedia.ws.common.error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

public class BadArgumentsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Collection<BadArgumentError> errors;
	
	public BadArgumentsException(Collection<BadArgumentError> errors) {
		this.errors = errors;
	}
	
	public BadArgumentsException() {
		this(new ArrayList<BadArgumentError>());
	}
	
	public BadArgumentsException(BadArgumentError error) {
		this(Arrays.asList(error));
	}
	
	public BadArgumentsException(String name,  String value) {
		this(new BadArgumentError(name, value));
	}
	
	public Collection<BadArgumentError> getBadArgumentErrors() {
		return errors;
	}
	
	@XmlRootElement(name="badArgument")
	public static class BadArgumentError {
		
		@XmlAttribute
		private String name;
		@XmlAttribute
		private String value;
		
		BadArgumentError() {
			// blank for jaxb marshaller
		}
		
		public BadArgumentError(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		public BadArgumentError(String name, Object value) {
			this(name, (value == null) ? null : value.toString());
		}
		
		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}
}
