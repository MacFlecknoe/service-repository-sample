package com.healthmedia.ws.security;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.UsernameToken;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;

public class MockTokenValidator implements Validator {

	@Override
	public Credential validate(Credential credential, RequestData data) throws WSSecurityException {
		
		UsernameToken usernameToken = credential.getUsernametoken();
		
		String username = usernameToken.getName();
		String password = usernameToken.getPassword();
		
		if("username".equals(username) && "password".equals(password)) {
			return credential;
		}
		throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
	}

}
