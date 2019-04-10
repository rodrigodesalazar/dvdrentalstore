package com.ongres.dvdrentalstore.dto;

public class OverdueRental
{
	private String customer;
	
	private String phone;
	
	private String title;

	public String getCustomer()
	{
		return customer;
	}

	public void setCustomer(String customer)
	{
		this.customer = customer;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
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
		return "OverdueRental [customer=" + customer + ", phone=" + phone + ", title=" + title + "]";
	}
}
