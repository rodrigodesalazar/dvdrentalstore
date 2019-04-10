package com.ongres.dvdrentalstore.dto;

public class GenericRequest
{
	private Integer customerID;
	
	private String title;

	public Integer getCustomerID()
	{
		return customerID;
	}

	public void setCustomerID(Integer customerID)
	{
		this.customerID = customerID;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String toString()
	{
		return "GenericRequest [customerID=" + customerID + ", title=" + title + "]";
	}
}
