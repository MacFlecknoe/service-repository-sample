package com.healthmedia.ws.common.error;

public class GenericErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String code;
	private final String message;
	
	public GenericErrorException(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
