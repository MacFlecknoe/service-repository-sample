package com.healthmedia.ws.common.error.v1;

public class InvalidQueryException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final String query;
	
	public InvalidQueryException(String query) {
		super(new StringBuilder("Invalid query: ").append(query).toString());
		this.query = query;
	}

	public String getQuery() {
		return query;
	}
}
