package com.ongres.dvdrentalstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.ongres.dvdrentalstore.dao.RentalDAO;
import com.ongres.dvdrentalstore.exception.DAOException;
import com.ongres.dvdrentalstore.exception.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RentalServiceTest
{
	@Value("${error.notAvailable}")
	private String notAvailable;
	
	@Value("${error.incorrectParameters}")
	private String incorrectParameters;
	
	@Autowired
	private IRentalService rentalService;

	@MockBean
	private RentalDAO rentalDAO;

	@Test
	public void rentDVDOk() throws DAOException
	{
		Integer inventoryID = 1;
		Integer customerID = 1;
		String staffName = "STORE GUY";
		String title = "MOVIE TITLE";

		List<Integer> inventoryIDs = Stream.of(inventoryID, inventoryID + 1, inventoryID + 2).collect(Collectors.toList());
		
		Mockito.when(rentalDAO.getInventoryIDs(customerID, title)).thenReturn(inventoryIDs);
		Mockito.when(rentalDAO.isAvailable(inventoryID)).thenReturn(true);

		try 
		{
			rentalService.rentDVD(customerID, staffName, title);
		}
		catch (ServiceException e) 
		{
			Assert.fail("Error in service: " + e.getMessage());
		}
	}
	
	@Test
	public void rentDVDError() throws DAOException
	{
		Integer inventoryID = 1;
		Integer customerID = 1;
		String staffName = "STORE GUY";
		String title = "MOVIE TITLE";
		String errorMessage = "ERROR MESSAGE";

		List<Integer> inventoryIDs = Stream.of(inventoryID, inventoryID + 1, inventoryID + 2).collect(Collectors.toList());
		
		Mockito.when(rentalDAO.getInventoryIDs(customerID, title)).thenReturn(inventoryIDs);
		Mockito.when(rentalDAO.isAvailable(inventoryID)).thenReturn(true);
		Mockito.doThrow(new DAOException(errorMessage)).when(rentalDAO).registerRentalAndPayment(customerID, staffName, inventoryID);

		try 
		{
			rentalService.rentDVD(customerID, staffName, title);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e)
		{
			Assert.assertTrue(e.getMessage().equals(errorMessage));
		}
	}
	
	@Test
	public void rentDVDNotAvailable() throws DAOException
	{
		Integer inventoryID = 1;
		Integer customerID = 1;
		String staffName = "STORE GUY";
		String title = "MOVIE TITLE";

		List<Integer> inventoryIDs = Stream.of(inventoryID, inventoryID + 1, inventoryID + 2).collect(Collectors.toList());
		
		Mockito.when(rentalDAO.getInventoryIDs(customerID, title)).thenReturn(inventoryIDs);
		Mockito.when(rentalDAO.isAvailable(inventoryID)).thenReturn(false);

		try 
		{
			rentalService.rentDVD(customerID, staffName, title);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e) 
		{
			Assert.assertTrue(e.getMessage().equals(notAvailable));
		}
	}
	
	@Test
	public void rentDVDEmptyInventory() throws DAOException
	{
		Integer inventoryID = 1;
		Integer customerID = 1;
		String staffName = "STORE GUY";
		String title = "MOVIE TITLE";

		List<Integer> inventoryIDs = new ArrayList<Integer>();
		
		Mockito.when(rentalDAO.getInventoryIDs(customerID, title)).thenReturn(inventoryIDs);
		Mockito.when(rentalDAO.isAvailable(inventoryID)).thenReturn(true);

		try 
		{
			rentalService.rentDVD(customerID, staffName, title);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e) 
		{
			Assert.assertTrue(e.getMessage().equals(notAvailable));
		}
	}
	
	@Test
	public void rentDVDIncorrectParameters() throws DAOException
	{
		rentDVDIncorrectParameters(null, null, null);
		rentDVDIncorrectParameters(null, "STORE GUY", "MOVIE TITLE");
		rentDVDIncorrectParameters(1, null, "MOVIE TITLE");
		rentDVDIncorrectParameters(1, "", "MOVIE TITLE");
		rentDVDIncorrectParameters(1, " ", "MOVIE TITLE");
		rentDVDIncorrectParameters(1, "STORE GUY", null);
		rentDVDIncorrectParameters(1, "STORE GUY", "");
		rentDVDIncorrectParameters(1, "STORE GUY", " ");
	}
	
	private void rentDVDIncorrectParameters(Integer customerID, String staffName, String title)
	{
		try 
		{
			rentalService.rentDVD(customerID, staffName, title);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e)
		{
			Assert.assertTrue(e.getMessage().equals(incorrectParameters));
		}		
	}
	
	@Test
	public void returnDVDOk() throws DAOException
	{
		Integer customerID = 1;
		String title = "MOVIE TITLE";

		try 
		{
			rentalService.returnDVD(customerID, title);
		}
		catch (ServiceException e) 
		{
			Assert.fail("Error in service: " + e.getMessage());
		}
	}
	
	@Test
	public void returnDVDError() throws DAOException
	{
		Integer customerID = 1;
		String title = "MOVIE TITLE";
		String errorMessage = "ERROR MESSAGE";
		
		Mockito.doThrow(new DAOException(errorMessage)).when(rentalDAO).updateRental(customerID, title);

		try 
		{
			rentalService.returnDVD(customerID, title);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e)
		{
			Assert.assertTrue(e.getMessage().equals(errorMessage));
		}
	}
	
	@Test
	public void returnDVDIncorrectParameters() throws DAOException
	{
		returnDVDIncorrectParameters(null, null);
		returnDVDIncorrectParameters(null, "MOVIE TITLE");
		returnDVDIncorrectParameters(1, null);
		returnDVDIncorrectParameters(1, "");
		returnDVDIncorrectParameters(1, " ");
	}
	
	private void returnDVDIncorrectParameters(Integer customerID, String title)
	{
		try 
		{
			rentalService.returnDVD(customerID, title);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e)
		{
			Assert.assertTrue(e.getMessage().equals(incorrectParameters));
		}		
	}
}