package com.ongres.dvdrentalstore.service;

import com.ongres.dvdrentalstore.exception.NotAvailableException;

public interface IRentalService
{
	Boolean rentDVD(Integer customerID, String staffName, String title) throws NotAvailableException;
	
	Boolean returnDVD(Integer customerID, String title);
}
