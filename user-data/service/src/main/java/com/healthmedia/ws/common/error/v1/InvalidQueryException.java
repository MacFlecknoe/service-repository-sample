package com.healthmedia.ws.common.error.v1;

public class InvalidQueryException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String query;
	
	public InvalidQueryException(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
