package com.ongres.dvdrentalstore.service;

import java.util.List;

import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.exception.ServiceException;

public interface IReportingService
{
	Integer clientsByCountry(String country, String city) throws ServiceException;
	
	List<Film> filmsByActor(String actor, String category) throws ServiceException;
	
	List<OverdueRental> overdueRentals() throws ServiceException;
}
