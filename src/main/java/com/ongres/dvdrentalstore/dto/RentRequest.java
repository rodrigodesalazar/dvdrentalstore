package com.ongres.dvdrentalstore.dto;

public class RentRequest extends GenericRequest
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

	@Override
	public String toString()
	{
		return "RentRequest [staffName=" + staffName + ", getCustomerID()=" + getCustomerID() + ", getTitle()=" + getTitle() + "]";
	}

}
