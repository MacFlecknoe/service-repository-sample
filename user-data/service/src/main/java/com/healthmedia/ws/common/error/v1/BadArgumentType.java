package com.healthmedia.ws.common.error.v1;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="badArgument")
public class BadArgumentType {

	protected String name;
	protected String value;
	
	public BadArgumentType() {
		// empty
	}
	
	public BadArgumentType(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}