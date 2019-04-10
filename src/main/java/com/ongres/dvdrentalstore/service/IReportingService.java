package com.ongres.dvdrentalstore.service;

import java.util.List;

import com.ongres.dvdrentalstore.dto.ClientCount;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.exception.ServiceException;

/**
 * Interface defining services related to generating store reports.
 * 
 * @author rodrigodesalazar
 *
 */
public interface IReportingService
{
	/**
	 * Returns number of clients in a given country, and optionally city.
	 * It is case insensitive, but it returns exact matches only.
	 *  
	 * @param country the country to search for.
	 * @param city city in the country (optional).
	 * @return the number of clients matching the country/city.
	 */
	ClientCount clientsByCountry(String country, String city) throws ServiceException;
	
	/**
	 * Returns films matching given actor name.
	 * The actor name is case insensitively matched against name, last name and the combination of both. 
	 * The resulting films are filtered by category, again case insensitively. 
	 * 
	 * @param actor the actor to look for.
	 * @param category category to which the film belongs to (optional).
	 * @return films matching given actor name.
	 */
	List<Film> filmsByActor(String actor, String category) throws ServiceException;
	
	/**
	 * Returns a list of clients with overdue rentals.
	 * 
	 * @return clients with overdue rentals.
	 */
	List<OverdueRental> overdueRentals() throws ServiceException;
}
