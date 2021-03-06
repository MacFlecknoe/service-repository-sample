package com.healthmedia.ws.common.error;

public enum ApplicationErrorCode {

	BAD_ARGUMENT("BadArgument"), 
	VALIDATION_ERROR("ValidationError"), 
	SERVER_ERROR("ServerError");

	private String code;

	private ApplicationErrorCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
