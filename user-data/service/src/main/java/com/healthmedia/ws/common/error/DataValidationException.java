package com.healthmedia.ws.common.error;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

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
	
	@XmlRootElement(name="dataValidationError")
	public static class DataValidationError {
		
		@XmlAttribute
		private String code;
		@XmlAttribute
		private String name;
		@XmlAttribute
		private String description;
		
		DataValidationError() {
			// blank for jaxb marshaller
		}
		
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
