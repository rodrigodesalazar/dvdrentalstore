package com.ongres.dvdrentalstore.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.sym.Name;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.dto.RentRequest;
import com.ongres.dvdrentalstore.dto.ReturnRequest;
import com.ongres.dvdrentalstore.exception.NotAvailableException;
import com.ongres.dvdrentalstore.service.IRentalService;
import com.ongres.dvdrentalstore.service.IReportingService;

@RestController
@RequestMapping(path = "/rental")
public class DVDRentalController
{

	private static final Logger logger = LoggerFactory.getLogger(DVDRentalController.class);

	@Autowired
	private IRentalService rentalService;

	@RequestMapping(path = "/rentDVD", method = RequestMethod.POST, consumes = "application/json")
	public Boolean rentDVDPOST(@RequestBody String json)
	{
		ObjectMapper mapper = new ObjectMapper();
		RentRequest request = null;
		try
		{
			request = mapper.readValue(json, RentRequest.class);
		} catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			 rentalService.rentDVD(request.getCustomerID(), request.getStaffName(), request.getTitle());
		} catch (NotAvailableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(path = "/returnDVD", method = RequestMethod.POST, consumes = "application/json")
	public Boolean returnDVDPOST(@RequestBody String json)
	{
		ObjectMapper mapper = new ObjectMapper();
		ReturnRequest request = null;
		try
		{
			request = mapper.readValue(json, ReturnRequest.class);
		} catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rentalService.returnDVD(request.getCustomerID(), request.getTitle());
	}
}