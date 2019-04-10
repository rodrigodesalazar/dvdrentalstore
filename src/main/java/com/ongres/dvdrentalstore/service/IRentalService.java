package com.ongres.dvdrentalstore.service;

import com.ongres.dvdrentalstore.exception.ServiceException;

public interface IRentalService
{
	void rentDVD(Integer customerID, String staffName, String title) throws ServiceException;
	
	void returnDVD(Integer customerID, String title) throws ServiceException;
}
