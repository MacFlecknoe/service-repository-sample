package com.healthmedia.ws.common.error;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public class GenericErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String code;
	private final Collection<ErrorMessage> messages;
	
	public GenericErrorException(String code, String message) {
		this(code, Arrays.asList(new ErrorMessage(Locale.ENGLISH, message)));
	}
	
	public GenericErrorException(String code, Collection<ErrorMessage> messages) {
		if(code == null || messages == null) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		this.code = code;
		this.messages = messages;
	}
	
	public String getCode() {
		return code;
	}
	
	public Collection<ErrorMessage> getErrorMessages() {
		return messages;
	}
	
	public ErrorMessage getErrorMessage(Locale locale) {
		for(ErrorMessage m : getErrorMessages()) {
			if(m.getLocale().equals(locale)) {
				return m;
			}
		}
		return null;
	}
	
	@Override
	public String getMessage() {
		ErrorMessage error = getErrorMessage(Locale.getDefault());
		if(error == null) {
			error = getErrorMessages().iterator().next();
		}
		return (error == null) ? null : error.getMessage();
	}
	
	public static class ErrorMessage {
		
		private final Locale language;
		private final String message;
		
		public ErrorMessage(Locale language, String message) {
			this.language = language;
			this.message = message;
		}

		public Locale getLocale() {
			return language;
		}

		public String getMessage() {
			return message;
		}
	}
}
