package com.ongres.dvdrentalstore.service;

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

import com.ongres.dvdrentalstore.dao.ReportingDAO;
import com.ongres.dvdrentalstore.dto.Actor;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.exception.DAOException;
import com.ongres.dvdrentalstore.exception.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportingServiceTest
{	
	@Value("${error.incorrectParameters}")
	private String incorrectParameters;
	
	@Autowired
	private IReportingService reportingService;

	@MockBean
	private ReportingDAO reportingDAO;

	@Test
	public void clientsByCountryOk() throws DAOException
	{
		Integer count = 10;
		String country = "COUNTRY";
		String city = null;
		
		Mockito.when(reportingDAO.getClientsByCountry(country, city)).thenReturn(count);

		try 
		{
			Assert.assertEquals(count, reportingService.clientsByCountry(country, city).getNumberOfClients());
		}
		catch (ServiceException e) 
		{
			Assert.fail("Error in service: " + e.getMessage());
		}
	}
	
	@Test
	public void clientsByCountryAndCityOk() throws DAOException
	{
		Integer count = 10;
		String country = "COUNTRY";
		String city = "CITY";
		
		Mockito.when(reportingDAO.getClientsByCountry(country, city)).thenReturn(count);

		try 
		{
			Assert.assertEquals(count, reportingService.clientsByCountry(country, city).getNumberOfClients());
		}
		catch (ServiceException e) 
		{
			Assert.fail("Error in service: " + e.getMessage());
		}
	}
	
	@Test
	public void clientsByCountryError() throws DAOException
	{
		String country = "COUNTRY";
		String city = null;
		String errorMessage = "ERROR MESSAGE";
		
		Mockito.when(reportingDAO.getClientsByCountry(country, city)).thenThrow(new DAOException(errorMessage));

		try 
		{
			reportingService.clientsByCountry(country, city);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e) 
		{
			Assert.assertTrue(e.getMessage().equals(errorMessage));
		}
	}
	
	@Test
	public void clientsByCountryIncorrectParameters() throws DAOException
	{
		incorrectParameters(null, null);
		incorrectParameters(null, "");
		incorrectParameters(null, " ");
		incorrectParameters(null, "CITY");
		incorrectParameters("", null);
		incorrectParameters("", "");
		incorrectParameters("", " ");
		incorrectParameters("", "CITY");
		incorrectParameters(" ", null);
		incorrectParameters(" ", "");
		incorrectParameters(" ", " ");
		incorrectParameters(" ", "CITY");
	}
	
	private void incorrectParameters(String country, String city)
	{
		try 
		{
			reportingService.clientsByCountry(country, city);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e)
		{
			Assert.assertTrue(e.getMessage().equals(incorrectParameters));
		}		
	}
	
	@Test
	public void filmsByActorOk() throws DAOException
	{
		Actor actor = new Actor();
		actor.setFirstName("FIRSTNAME");
		actor.setLastName("LASTTNAME");
		String category1 = "CATEGORY1";
		String category2 = "CATEGORY2";
		Film film1 = new Film();
		film1.setActor(actor);
		film1.setCategoryName(category1);
		Film film2 = new Film();
		film2.setActor(actor);
		film2.setCategoryName(category2);
		List<Film> films = Stream.of(film1, film2).collect(Collectors.toList());
		
		Mockito.when(reportingDAO.getFilmsByActor("NAME")).thenReturn(films);

		try 
		{
			Assert.assertEquals(films, reportingService.filmsByActor("NAME", null));
		}
		catch (ServiceException e) 
		{
			Assert.fail("Error in service: " + e.getMessage());
		}
	}
	
	@Test
	public void filmsByActorAndCategoryOk() throws DAOException
	{
		Actor actor = new Actor();
		actor.setFirstName("FIRSTNAME");
		actor.setLastName("LASTTNAME");
		String category1 = "CATEGORY1";
		String category2 = "CATEGORY2";
		Film film1 = new Film();
		film1.setActor(actor);
		film1.setCategoryName(category1);
		Film film2 = new Film();
		film2.setActor(actor);
		film2.setCategoryName(category2);
		List<Film> films = Stream.of(film1, film2).collect(Collectors.toList());
		List<Film> filmsFilteredByCategory = Stream.of(film1).collect(Collectors.toList());
		
		Mockito.when(reportingDAO.getFilmsByActor("NAME")).thenReturn(films);

		try 
		{
			Assert.assertEquals(filmsFilteredByCategory, reportingService.filmsByActor("NAME", category1));
		}
		catch (ServiceException e) 
		{
			Assert.fail("Error in service: " + e.getMessage());
		}
	}
	
	@Test
	public void filmsByActorError() throws DAOException
	{
		String actor = "NAME";
		String category = null;
		String errorMessage = "ERROR MESSAGE";
		
		Mockito.when(reportingDAO.getFilmsByActor(actor)).thenThrow(new DAOException(errorMessage));

		try 
		{
			reportingService.filmsByActor(actor, category);
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e) 
		{
			Assert.assertTrue(e.getMessage().equals(errorMessage));
		}
	}
	
	@Test
	public void filmsByActorIncorrectParameters() throws DAOException
	{
		incorrectParameters(null, null);
		incorrectParameters(null, "");
		incorrectParameters(null, " ");
		incorrectParameters(null, "CATEGORY");
		incorrectParameters("", null);
		incorrectParameters("", "");
		incorrectParameters("", " ");
		incorrectParameters("", "CATEGORY");
		incorrectParameters(" ", null);
		incorrectParameters(" ", "");
		incorrectParameters(" ", " ");
		incorrectParameters(" ", "CATEGORY");
	}
	
	@Test
	public void overdueRentalsOk() throws DAOException
	{
		OverdueRental overdueRental1 = new OverdueRental();
		overdueRental1.setCustomer("CUSTOMER1");
		overdueRental1.setPhone("PHONE1");
		overdueRental1.setTitle("TITLE1");
		OverdueRental overdueRental2 = new OverdueRental();
		overdueRental2.setCustomer("CUSTOMER2");
		overdueRental2.setPhone("PHONE2");
		overdueRental2.setTitle("TITLE2");
		
		List<OverdueRental> overdueRentals = Stream.of(overdueRental1, overdueRental2).collect(Collectors.toList());
		
		Mockito.when(reportingDAO.getOverdueRentals()).thenReturn(overdueRentals);

		try 
		{
			Assert.assertEquals(overdueRentals, reportingService.overdueRentals());
		}
		catch (ServiceException e) 
		{
			Assert.fail("Error in service: " + e.getMessage());
		}
	}
	
	@Test
	public void overdueRentalsError() throws DAOException
	{
		String errorMessage = "ERROR MESSAGE";
		
		Mockito.when(reportingDAO.getOverdueRentals()).thenThrow(new DAOException(errorMessage));

		try 
		{
			reportingService.overdueRentals();
			Assert.fail("The service should have failed.");
		}
		catch (ServiceException e) 
		{
			Assert.assertTrue(e.getMessage().equals(errorMessage));
		}
	}
}