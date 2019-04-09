package com.ongres.dvdrentalstore.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ongres.dvdrentalstore.dto.ClientCount;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.service.IReportingService;

@RestController
@RequestMapping(path = "/reporting")
public class DVDReportingController
{

	private static final Logger logger = LoggerFactory.getLogger(DVDReportingController.class);

	@Autowired
	private IReportingService reportingService;

	@RequestMapping(path = "/clientsByCountry", method = RequestMethod.GET)
	public ClientCount clientsByCountryGET(@RequestParam(value = "country") String country, @RequestParam(value = "city", required = false) String city)
	{
		logger.debug("country: " + country);
		logger.debug("city: " + city);
		ClientCount clientCount = new ClientCount();
		clientCount.setNumberOfClients(reportingService.clientsByCountry(country, city));
		return clientCount;
	}

	@RequestMapping(path = "/filmsByActor", method = RequestMethod.GET)
	public List<Film> filmsByActorGET(@RequestParam(value = "actor") String actor, @RequestParam(value = "category", required = false) String category)
	{
		logger.debug("actor: " + actor);
		logger.debug("category: " + category);
		return reportingService.filmsByActor(actor, category);
	}

	@RequestMapping(path = "/overdueRentals", method = RequestMethod.GET)
	public List<OverdueRental> overdueRentalsGET()
	{

		return reportingService.overdueRentals();
	}

}