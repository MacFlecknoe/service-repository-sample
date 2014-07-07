package com.healthmedia.ws.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * Looks up passwords for the private keys placed in a keystore.
 * 
 * @author mlamber7
 *
 */
public class KeystorePasswordCallback implements CallbackHandler {

	private Map<String, String> passwords;

	public KeystorePasswordCallback(Map<String, String> passwords) {
		this.passwords = passwords;
	}
	
	public KeystorePasswordCallback() {
		this(Collections.singletonMap("serveralias", "password"));
	}
	
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

			String pass = passwords.get(pc.getIdentifier().toLowerCase());
			
			if (pass != null) {
				pc.setPassword(pass);
				return;
			}
		}
	}
}