package com.ongres.dvdrentalstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongres.dvdrentalstore.dao.RentalDAO;
import com.ongres.dvdrentalstore.exception.NotAvailableException;

@Service
public class RentalService implements IRentalService
{
	@Autowired
	private RentalDAO rentalDAO;

	@Override
	@Transactional
	public Boolean rentDVD(Integer customerID, String staffName, String title) throws NotAvailableException
	{
		List<Integer> inventoryIDs = rentalDAO.getInventoryIDs(customerID, title);
		
		Optional<Integer> inventoryID = inventoryIDs.stream().filter(x -> rentalDAO.isAvailable(x)).findFirst();
		
		if (inventoryID.isPresent())
		{
			Integer rentalID = rentalDAO.rentDVD(customerID, staffName, inventoryID.get());
			
			Double customerBalance = rentalDAO.getCustomerBalance(customerID);
			
			rentalDAO.registerBalance(customerID, staffName, rentalID, customerBalance);		
		}
		else
		{
			throw new NotAvailableException();
		}
		
		
		return null;
	}

	@Override
	@Transactional
	public Boolean returnDVD(Integer customerID, String title)
	{
		
		rentalDAO.returnDVD(customerID, title);
		
		return null;
	}

}
