package org.solmix.service.jackson;

public class InternalBean {

	private String resource;
	
	public InternalBean(String resource){
		this.setResource(resource);
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
}
