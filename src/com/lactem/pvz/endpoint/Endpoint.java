package com.lactem.pvz.endpoint;

import java.io.Serializable;

public class Endpoint implements Serializable {
	private static final long serialVersionUID = -875875873205128441L;
	private String location;

	public Endpoint(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}