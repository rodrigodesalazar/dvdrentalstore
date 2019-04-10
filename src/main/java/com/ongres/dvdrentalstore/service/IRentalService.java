package com.ongres.dvdrentalstore.service;

import com.ongres.dvdrentalstore.exception.ServiceException;

/**
 * Interface defining services related to renting DVDs.
 * 
 * @author rodrigodesalazar
 *
 */
public interface IRentalService
{
	/**
	 * Given a title, customer id and staff name, rent a DVD.
	 * 
	 * @param customerID id of the customer renting a film.
	 * @param staffName name of the store clerk serving the customer.
	 * @param title title of the film.
	 * @throws ServiceException in case the renting goes wrong.
	 */
	void rentDVD(Integer customerID, String staffName, String title) throws ServiceException;
	
	/**
	 * Given a title and customer id return a DVD.
	 * 
	 * @param customerID id of the customer making the return.
	 * @param title title of the film.
	 * @throws ServiceException in case the return goes wrong.
	 */
	void returnDVD(Integer customerID, String title) throws ServiceException;
}
