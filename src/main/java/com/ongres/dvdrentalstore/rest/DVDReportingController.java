package com.ongres.dvdrentalstore.rest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ongres.dvdrentalstore.dto.GenericResponse;
import com.ongres.dvdrentalstore.exception.ServiceException;
import com.ongres.dvdrentalstore.service.IReportingService;

/**
 * This class contains REST services related to generating store reports.
 * 
 * @author rodrigodesalazar
 *
 */
@RestController
@RequestMapping(path = "/reporting")
public class DVDReportingController
{
	private static final Logger logger = LoggerFactory.getLogger(DVDReportingController.class);

	@Value("${status.ok}")
	private String statusOK;
	
	@Value("${status.error}")
	private String statusError;
	
	@Autowired
	private IReportingService reportingService;

	/**
	 * Returns number of clients in a given country, and optionally city.
	 * It is case insensitive, but it returns exact matches only.
	 *  
	 * @param country the country to search for.
	 * @param city city in the country (optional).
	 * @return the number of clients matching the country/city.
	 */
	@RequestMapping(path = "/clientsByCountry", method = RequestMethod.GET)
	public GenericResponse clientsByCountryGET(@RequestParam(value = "country") String country, @RequestParam(value = "city", required = false) String city)
	{
		logger.info("Enter: clientsByCountryGET");
		logger.info("country: " + country);
		logger.info("city: " + city);
		
		GenericResponse response = new GenericResponse();
		
		try
		{	
			response.setStatus(statusOK);
			response.setBody(reportingService.clientsByCountry(country, city));
		} 
		catch (ServiceException e)
		{
			response.setStatus(statusError);
			response.setBody(e.getMessage());
		} 
		catch (RuntimeException e)
		{
			logger.error(ExceptionUtils.getStackTrace(e));
			response.setStatus(statusError);
			response.setBody(e.getMessage());
		} 
		
		logger.info("Exit: clientsByCountryGET");
		logger.info("response: " + response);
		return response;
	}

	/**
	 * Returns films matching given actor name.
	 * The actor name is case insensitively matched against name, last name and the combination of both. 
	 * 
	 * @param actor the actor to look for.
	 * @param category category to which the film belongs to (optional).
	 * @return films matching given actor name.
	 */
	@RequestMapping(path = "/filmsByActor", method = RequestMethod.GET)
	public GenericResponse filmsByActorGET(@RequestParam(value = "actor") String actor, @RequestParam(value = "category", required = false) String category)
	{
		logger.info("Enter: filmsByActorGET");
		logger.info("actor: " + actor);
		logger.info("category: " + category);
		
		GenericResponse response = new GenericResponse();
		
		try
		{	
			response.setStatus(statusOK);
			response.setBody(reportingService.filmsByActor(actor, category));
		} 
		catch (ServiceException e)
		{
			response.setStatus(statusError);
			response.setBody(e.getMessage());
		} 
		catch (RuntimeException e)
		{
			logger.error(ExceptionUtils.getStackTrace(e));
			response.setStatus(statusError);
			response.setBody(e.getMessage());
		} 
		
		logger.info("Exit: filmsByActorGET");
		logger.info("response: " + response);
		return response;
	}

	/**
	 * Returns a list of clients with overdue rentals.
	 * 
	 * @return clients with overdue rentals.
	 */
	@RequestMapping(path = "/overdueRentals", method = RequestMethod.GET)
	public GenericResponse overdueRentalsGET()
	{
		logger.info("Enter: overdueRentalsGET");
		
		GenericResponse response = new GenericResponse();
		
		try
		{	
			response.setStatus(statusOK);
			response.setBody(reportingService.overdueRentals());
		} 
		catch (ServiceException e)
		{
			response.setStatus(statusError);
			response.setBody(e.getMessage());
		} 
		catch (RuntimeException e)
		{
			logger.error(ExceptionUtils.getStackTrace(e));
			response.setStatus(statusError);
			response.setBody(e.getMessage());
		} 
		
		logger.info("Exit: overdueRentalsGET");
		logger.info("response: " + response);
		return response;
	}

}