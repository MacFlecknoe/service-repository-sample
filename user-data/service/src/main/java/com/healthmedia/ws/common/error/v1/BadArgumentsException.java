package com.healthmedia.ws.common.error.v1;

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
	
	public BadArgumentsException(BadArgumentError error) {
		this(Arrays.asList(error));
	}
	
	public BadArgumentsException(String name,  String value) {
		this(new BadArgumentError(name, value));
	}
	
	public Collection<BadArgumentError> getErrorMessages() {
		return errors;
	}
	
	// TODO override getMessage
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
		
		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}
}
