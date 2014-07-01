package com.healthmedia.ws.common.error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
	
	public static class BadArgumentError {
		
		private String name;
		private String value;
		
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
