package com.ongres.dvdrentalstore.dto;

public class GenericResponse 
{
	private String status;
	
	private Object body;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
}
