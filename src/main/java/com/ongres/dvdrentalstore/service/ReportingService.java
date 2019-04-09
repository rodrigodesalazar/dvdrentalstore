package com.ongres.dvdrentalstore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ongres.dvdrentalstore.dao.ReportingDAO;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;

@Service
public class ReportingService implements IReportingService
{
	@Autowired
	private ReportingDAO reportingDAO;

	@Override
	public Integer clientsByCountry(String country, String city)
	{

		return reportingDAO.clientsByCountry(country, city);
	}

	@Override
	public List<Film> filmsByActor(String actor, String category)
	{
		List<Film> filmsByActor = reportingDAO.filmsByActor(actor);

		if (category != null)
		{
			filmsByActor = filmsByActor.stream().filter(x -> category.toUpperCase().equals(x.getCategoryName().toUpperCase())).collect(Collectors.toList());
		}

		return filmsByActor;
	}

	@Override
	public List<OverdueRental> overdueRentals()
	{
		return reportingDAO.overdueRentals();
	}

}
