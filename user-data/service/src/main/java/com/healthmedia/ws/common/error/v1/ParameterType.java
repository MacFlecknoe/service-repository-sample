package com.healthmedia.ws.common.error.v1;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="parameter")
public class ParameterType {

	protected String name;
	protected String value;
	
	public ParameterType() {
		// empty
	}
	
	public ParameterType(String name, String value) {
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