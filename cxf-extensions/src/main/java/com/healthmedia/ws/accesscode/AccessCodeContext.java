package com.healthmedia.ws.accesscode;

public class AccessCodeContext {
	
	private static ThreadLocal<String> accessCodeContainer = new ThreadLocal<String>();
	
	public static String getAccessCode() {
		return accessCodeContainer.get();
	}
	
	public static void setAccessCode(String accessCode) {
		accessCodeContainer.set(accessCode);
	}
}