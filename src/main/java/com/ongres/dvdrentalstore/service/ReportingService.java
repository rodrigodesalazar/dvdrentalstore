package com.ongres.dvdrentalstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ongres.dvdrentalstore.dao.ReportingDAO;
import com.ongres.dvdrentalstore.dto.ClientCount;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.exception.DAOException;
import com.ongres.dvdrentalstore.exception.ServiceException;

/**
 * Service implementing the IReportingService interface.
 * 
 * @author rodrigodesalazar
 *
 */
@Service
public class ReportingService implements IReportingService
{
	private static final Logger logger = LoggerFactory.getLogger(ReportingService.class);
	
	@Value("${error.incorrectParameters}")
	private String incorrectParameters;
	
	@Autowired
	private ReportingDAO reportingDAO;
	
	/* (non-Javadoc)
	 * @see com.ongres.dvdrentalstore.service.IReportingService#clientsByCountry(java.lang.String, java.lang.String)
	 */
	@Override
	public ClientCount clientsByCountry(String country, String city) throws ServiceException
	{
		logger.info("Enter: clientsByCountry");
		logger.info("country: " + country);
		logger.info("city: " + city);
		
		if (country == null || country.trim().isEmpty())
		{
			logger.error(incorrectParameters);
			throw new ServiceException(incorrectParameters);
		}
		
		ClientCount clientCount = null;

		try 
		{
			clientCount = new ClientCount(reportingDAO.getClientsByCountry(country, city));
		}
		catch (DAOException e)
		{
			throw new ServiceException(e.getMessage());
		}
		
		logger.info("Exit: clientsByCountry");
		logger.info("numberOfClients: " + clientCount);
		return clientCount;
				
				
	}

	/* (non-Javadoc)
	 * @see com.ongres.dvdrentalstore.service.IReportingService#filmsByActor(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Film> filmsByActor(String actor, String category) throws ServiceException
	{
		logger.info("Enter: filmsByActor");
		logger.info("actor: " + actor);
		logger.info("category: " + category);
		
		if (actor == null || actor.trim().isEmpty())
		{
			logger.error(incorrectParameters);
			throw new ServiceException(incorrectParameters);
		}
		
		List<Film> filmsByActor = new ArrayList<Film>();
		
		try 
		{
			filmsByActor = reportingDAO.getFilmsByActor(actor);
		}
		catch (DAOException e) 
		{
			throw new ServiceException(e.getMessage());
		}

		if (category != null && !category.trim().isEmpty())
		{
			filmsByActor = filmsByActor.stream().filter(x -> category.toUpperCase().equals(x.getCategoryName().toUpperCase())).collect(Collectors.toList());
		}

		logger.info("Exit: filmsByActor");
		logger.info("filmsByActor: " + filmsByActor);
		return filmsByActor;
	}

	/* (non-Javadoc)
	 * @see com.ongres.dvdrentalstore.service.IReportingService#overdueRentals()
	 */
	@Override
	public List<OverdueRental> overdueRentals() throws ServiceException
	{
		logger.info("Enter: overdueRentals");
		
		List<OverdueRental> overdueRentals = new ArrayList<OverdueRental>();
		
		try 
		{
			overdueRentals = reportingDAO.getOverdueRentals();
		}
		catch (DAOException e) 
		{
			throw new ServiceException(e.getMessage());
		}
		
		logger.info("Exit: overdueRentals");
		logger.info("overdueRentals: " + overdueRentals);
		return overdueRentals;
	}

}
