package com.healthmedia.ws.security.callback;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class MockPasswordCallbackHandler implements CallbackHandler {
	
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		
		if (pc.getIdentifier().equals("username")) {
			//
			// set the password on the callback. This will later be compared to
			// the password which was sent from the client.
			//
			pc.setPassword("password");
		}
	}
}