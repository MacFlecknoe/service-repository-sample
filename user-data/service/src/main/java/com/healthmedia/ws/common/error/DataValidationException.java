package com.healthmedia.ws.common.error;

import java.util.ArrayList;
import java.util.Collection;

public class DataValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String code;
	private final String description;
	private final Collection<DataValidationError> errors;
	
	public DataValidationException(String code, String description, Collection<DataValidationError> errors) {
		super(description);
		this.code = code;
		this.description = description;
		this.errors = errors;
	}
	
	public DataValidationException(String code, String description) {
		this(code, description, new ArrayList<DataValidationError>());
	}
	
	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Collection<DataValidationError> getDataValidationErrors() {
		return errors;
	}
	
	public static class DataValidationError {
		
		private String code;
		private String name;
		private String description;
		
		public DataValidationError(String code, String name, String description) {
			this.code = code;
			this.name = name;
			this.description = description;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
	}

}
