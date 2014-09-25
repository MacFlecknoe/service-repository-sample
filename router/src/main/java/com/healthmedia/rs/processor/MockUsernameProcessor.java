package com.healthmedia.rs.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MockUsernameProcessor implements Processor {

	private final String username;
	
	public MockUsernameProcessor(String username) {
		this.username = username;
	}
	
	public MockUsernameProcessor() {
		this("username");
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().setHeader("X-ExternalCustomerId", username);
	}

}
