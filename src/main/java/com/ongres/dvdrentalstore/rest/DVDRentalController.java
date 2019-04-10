package com.ongres.dvdrentalstore.rest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ongres.dvdrentalstore.dto.GenericRequest;
import com.ongres.dvdrentalstore.dto.GenericResponse;
import com.ongres.dvdrentalstore.dto.RentRequest;
import com.ongres.dvdrentalstore.exception.ServiceException;
import com.ongres.dvdrentalstore.service.IRentalService;

/**
 * This class contains REST services related to renting DVDs.
 * 
 * @author rodrigodesalazar
 *
 */
@RestController
@RequestMapping(path = "/rental")
public class DVDRentalController
{
	private static final Logger logger = LoggerFactory.getLogger(DVDRentalController.class);

	@Value("${status.ok}")
	private String statusOK;
	
	@Value("${status.error}")
	private String statusError;
	
	@Value("${body.successfulRent}")
	private String successfulRent;
	
	@Value("${body.successfulReturn}")
	private String successfulReturn;
	
	@Autowired
	private IRentalService rentalService;

	/**
	 * Given a title, customer id and staff name, rent a DVD.
	 * 
	 * @param request contains the aforementioned data.
	 * @return an Ok response if the rental is successful, Error otherwise.
	 */
	@RequestMapping(path = "/rentDVD", method = RequestMethod.POST, consumes = "application/json")
	public GenericResponse rentDVDPOST(@RequestBody RentRequest request)
	{
		logger.info("Enter: rentDVDPOST");
		logger.info("request: " + request);

		GenericResponse response = new GenericResponse();
		
		try
		{	
			rentalService.rentDVD(request.getCustomerID(), request.getStaffName(), request.getTitle());
			
			response.setStatus(statusOK);
			response.setBody(successfulRent);
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
		
		logger.info("Exit: rentDVDPOST");
		logger.info("response: " + response);
		return response;
	}

	/**
	 * Given a title and customer id return a DVD.
	 * 
	 * @param request contains the aforementioned data.
	 * @return an Ok response if the return is successful, Error otherwise.
	 */
	@RequestMapping(path = "/returnDVD", method = RequestMethod.POST, consumes = "application/json")
	public GenericResponse returnDVDPOST(@RequestBody GenericRequest request)
	{
		logger.info("Exit: returnDVDPOST");
		logger.info("request: " + request);

		GenericResponse response = new GenericResponse();
		
		try
		{
			rentalService.returnDVD(request.getCustomerID(), request.getTitle());
			
			response.setStatus(statusOK);
			response.setBody(successfulReturn);
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
		
		logger.info("Exit: returnDVDPOST");
		logger.info("response: " + response);
		return response;
	}
}