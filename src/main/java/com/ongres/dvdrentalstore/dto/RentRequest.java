package com.ongres.dvdrentalstore.dto;

public class RentRequest extends GeneralRequest
{
	private String staffName;
	
	public String getStaffName()
	{
		return staffName;
	}

	public void setStaffName(String staffName)
	{
		this.staffName = staffName;
	}

}
