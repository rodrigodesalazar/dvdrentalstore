package com.ongres.dvdrentalstore.dto;

public class ClientCount
{
	private Integer numberOfClients;

	public Integer getNumberOfClients()
	{
		return numberOfClients;
	}

	public void setNumberOfClients(Integer numberOfClients)
	{
		this.numberOfClients = numberOfClients;
	}

	@Override
	public String toString()
	{
		return "ClientCount [numberOfClients=" + numberOfClients + "]";
	}
}
