package com.ongres.dvdrentalstore.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongres.dvdrentalstore.dao.RentalDAO;
import com.ongres.dvdrentalstore.exception.DAOException;
import com.ongres.dvdrentalstore.exception.ServiceException;

@Service
public class RentalService implements IRentalService
{
	private static final Logger logger = LoggerFactory.getLogger(RentalService.class);
	
	@Value("${warning.notAvailable}")
	private String warningNotAvailable;
	
	@Value("${error.incorrectParameters}")
	private String incorrectParameters;
	
	@Autowired
	private RentalDAO rentalDAO;

	@Override
	@Transactional
	public void rentDVD(Integer customerID, String staffName, String title) throws ServiceException
	{
		logger.info("Enter: rentDVD");
		logger.info("customerID: " + customerID);
		logger.info("staffName: " + staffName);
		logger.info("title: " + title);
		
		if (customerID == null || staffName == null || title == null)
		{
			logger.error(incorrectParameters);
			throw new ServiceException(incorrectParameters);
		}
		
		List<Integer> inventoryIDs;
		
		try 
		{
			inventoryIDs = rentalDAO.getInventoryIDs(customerID, title);


			Optional<Integer> inventoryID = inventoryIDs.stream().filter(x -> {
					try { return rentalDAO.isAvailable(x); } 
					catch (DAOException e) { throw new RuntimeException(e.getMessage()); }
				}).findFirst();

			if (inventoryID.isPresent())
			{
				Integer rentalID = rentalDAO.registerRental(customerID, staffName, inventoryID.get());

				Double customerBalance = rentalDAO.getCustomerBalance(customerID);

				rentalDAO.registerPayment(customerID, staffName, rentalID, customerBalance);		
			}
			else
			{
				logger.warn(warningNotAvailable);
				throw new ServiceException(warningNotAvailable);
			}

		} 
		catch (DAOException e) 
		{
			throw new ServiceException(e.getMessage());
		}
		
		logger.info("Exit: rentDVD");
	}

	@Override
	@Transactional
	public void returnDVD(Integer customerID, String title) throws ServiceException
	{
		logger.info("Enter: returnDVD");
		logger.info("customerID: " + customerID);
		logger.info("title: " + title);
		
		if (customerID == null || title == null)
		{
			logger.error(incorrectParameters);
			throw new ServiceException(incorrectParameters);
		}
		
		try 
		{
			rentalDAO.updateRental(customerID, title);
		}
		catch (DAOException e) 
		{
			throw new ServiceException(e.getMessage());
		}
		
		logger.info("Exit: returnDVD");
	}
}
