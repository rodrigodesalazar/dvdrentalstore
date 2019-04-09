package com.ongres.dvdrentalstore.dto;

public class ClientCount
{
	private Integer numberOfClients;

	private String errorMessage;

	public Integer getNumberOfClients()
	{
		return numberOfClients;
	}

	public void setNumberOfClients(Integer numberOfClients)
	{
		this.numberOfClients = numberOfClients;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
}
