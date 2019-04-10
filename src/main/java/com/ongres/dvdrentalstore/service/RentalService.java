package com.ongres.dvdrentalstore.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ongres.dvdrentalstore.dao.RentalDAO;
import com.ongres.dvdrentalstore.exception.DAOException;
import com.ongres.dvdrentalstore.exception.ServiceException;

/**
 * Service implementing the IRentalService interface.
 * 
 * @author rodrigodesalazar
 *
 */
@Service
public class RentalService implements IRentalService
{
	private static final Logger logger = LoggerFactory.getLogger(RentalService.class);
	
	@Value("${error.notAvailable}")
	private String notAvailable;
	
	@Value("${error.incorrectParameters}")
	private String incorrectParameters;
	
	@Autowired
	private RentalDAO rentalDAO;

	/* (non-Javadoc)
	 * @see com.ongres.dvdrentalstore.service.IRentalService#rentDVD(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
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
			// As there can be several copies of a film, we take the first one we find.
			inventoryIDs = rentalDAO.getInventoryIDs(customerID, title);

			Optional<Integer> inventoryID = inventoryIDs.stream().filter(x -> {
					try { return rentalDAO.isAvailable(x); } 
					catch (DAOException e) { throw new RuntimeException(e.getMessage()); }
				}).findFirst();

			if (inventoryID.isPresent())
			{
				rentalDAO.registerRentalAndPayment(customerID, staffName, inventoryID.get());
			}
			else
			{
				logger.error(notAvailable);
				throw new ServiceException(notAvailable);
			}

		} 
		catch (DAOException e) 
		{
			throw new ServiceException(e.getMessage());
		}
		
		logger.info("Exit: rentDVD");
	}

	/* (non-Javadoc)
	 * @see com.ongres.dvdrentalstore.service.IRentalService#returnDVD(java.lang.Integer, java.lang.String)
	 */
	@Override
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
