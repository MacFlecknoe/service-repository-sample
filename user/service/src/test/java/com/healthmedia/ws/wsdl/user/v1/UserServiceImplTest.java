package com.healthmedia.ws.wsdl.user.v1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.junit.Ignore;
import org.junit.Test;


//import com.arjuna.mw.wst11.UserTransaction;
//import com.arjuna.mw.wst11.UserTransactionFactory;
//import com.arjuna.mw.wst11.client.JaxWSHeaderContextProcessor;
//import com.arjuna.webservices11.wsat.server.ParticipantInitialisation;
import com.healthmedia.ws.wsdl.soap.user.v1.UserSoapServicePorts;

public class UserServiceImplTest {

	@Ignore
	@Test
	public void test() throws Error {
		
		UserService client = new UserSoapServicePorts().getUserSoapServicePort();
		
		BindingProvider bindingProvider = (BindingProvider) client;
		List<Handler> handlers = new ArrayList<Handler>(1);
		bindingProvider.getBinding().setHandlerChain(handlers);
		
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/user-service/userService");
		
		client.importUser(null);

		fail("Not yet implemented");
	}

}
