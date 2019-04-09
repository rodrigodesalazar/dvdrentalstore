package com.ongres.dvdrentalstore.service;

import java.util.List;

import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;

public interface IReportingService
{
	Integer clientsByCountry(String country, String city);
	
	List<Film> filmsByActor(String actor, String category);
	
	List<OverdueRental> overdueRentals();
}
