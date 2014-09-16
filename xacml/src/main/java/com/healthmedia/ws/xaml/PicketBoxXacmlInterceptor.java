package com.healthmedia.ws.xaml;

import org.apache.cxf.message.Message;
import org.apache.cxf.rt.security.xacml.AbstractXACMLAuthorizingInterceptor;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;

/**
 * https://developer.jboss.org/wiki/PicketBoxXACMLSimpleWalkThrough
 * 
 * @author mlamber7
 *
 */
public class PicketBoxXacmlInterceptor extends AbstractXACMLAuthorizingInterceptor {

	@Override
	public ResponseType performRequest(RequestType request, Message message) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
