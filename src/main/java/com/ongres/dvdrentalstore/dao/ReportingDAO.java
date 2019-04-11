package com.ongres.dvdrentalstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ongres.dvdrentalstore.dto.Actor;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.exception.DAOException;

/**
 * This class deals with database access related to generating store reports.
 * 
 * @author rodrigodesalazar
 *
 */
@Repository
public class ReportingDAO
{
	private static final Logger logger = LoggerFactory.getLogger(ReportingDAO.class);
	
	@Value("${database.username}")
	private String username;
	
	@Value("${database.password}")
	private String password;
	
	@Value("${database.url}")
	private String url;

	/**
	 * Returns number of clients in a given country, and optionally city.
	 * It is case insensitive, but it returns exact matches only.
	 * It would make no sense to count partial matches (to which country would each number belong to then?).
	 * Keep an eye on performance.
	 *  
	 * @param country the country to search for.
	 * @param city city in the country (optional).
	 * @return the number of clients matching the country/city.
	 */
	public Integer getClientsByCountry(String country, String city) throws DAOException
	{
		logger.info("Enter: getClientsByCountry");
		logger.info("country: " + country);
		logger.info("city: " + city);
		
		Connection connection = null;
		Integer numberOfClients = null;
		
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			
			StringBuilder query = new StringBuilder(
					"SELECT COUNT(*) AS clients FROM customer AS c "
					+ "JOIN address AS a USING (address_id) "
					+ "JOIN city AS ci USING (city_id) "
					+ "JOIN country AS co USING (country_id) WHERE upper(co.country)=?");
			if (city != null && !city.trim().isEmpty())
			{
				query.append(" and upper(ci.city)=?");
			}
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setString(1, country.toUpperCase());
			if (city != null && !city.trim().isEmpty())
			{
				statement.setString(2, city.toUpperCase());
			}
			
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			numberOfClients = resultSet.getInt("clients");
		} 
		catch (SQLException e)
		{
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new DAOException(e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
				{
					connection.close();
				}
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}

		logger.info("Exit: getClientsByCountry");
		logger.info("numberOfClients: " + numberOfClients);
		return numberOfClients;
	}
	
	/**
	 * Returns films matching given actor name.
	 * The actor name is case insensitively matched against name, last name and the combination of both.
	 * Keep an eye on performance. 
	 * 
	 * @param actor the actor to look for.
	 * @return films matching given actor name.
	 */
	public List<Film> getFilmsByActor(String actor) throws DAOException
	{
		logger.info("Enter: getFilmsByActor");
		logger.info("actor: " + actor);
		
		Connection connection = null;
		List<Film> filmsByActor = new ArrayList<Film>();
		
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			
			String query = new String(
					"SELECT a.first_name AS firstname, a.last_name AS lastname, f.title AS title, f.description AS description, c.name AS categoryname "
					+ "FROM actor AS a "
					+ "JOIN film_actor AS fa USING (actor_id) "
					+ "JOIN film AS f USING (film_id) "
					+ "JOIN film_category AS fc USING (film_id) "
					+ "JOIN category AS c USING (category_id) "
					+ "WHERE a.first_name LIKE ? OR a.last_name LIKE ? OR a.first_name||' '||a.last_name LIKE ?");
			
			StringBuilder actorWildcard = new StringBuilder("%" + actor.toUpperCase() + "%");
			
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, actorWildcard.toString());
			statement.setString(2, actorWildcard.toString());
			statement.setString(3, actorWildcard.toString());
						
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next())
			{
				Film filmByActor = new Film();
				
				Actor actorFound = new Actor();
				actorFound.setFirstName(resultSet.getString("firstname"));
				actorFound.setLastName(resultSet.getString("lastname"));
				
				filmByActor.setActor(actorFound);
				filmByActor.setTitle(resultSet.getString("title"));
				filmByActor.setDescription(resultSet.getString("description"));
				filmByActor.setCategoryName(resultSet.getString("categoryname"));
				
				filmsByActor.add(filmByActor);
			}
		} 
		catch (SQLException e)
		{
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new DAOException(e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
				{
					connection.close();
				}
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}

		logger.info("Exit: getFilmsByActor");
		logger.info("filmsByActor: " + filmsByActor);
		return filmsByActor;
	}

	/**
	 * Returns a list of clients with overdue rentals.
	 * 
	 * @return clients with overdue rentals.
	 */
	public List<OverdueRental> getOverdueRentals() throws DAOException
	{
		logger.info("Enter: getOverdueRentals");
		
		Connection connection = null;
		List<OverdueRental> overdueRentals = new ArrayList<OverdueRental>();
		
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			
			String query = new String(
					"SELECT c.last_name||', '||c.first_name AS customer, a.phone AS phone, f.title AS title "
					+ "FROM rental AS r "
					+ "JOIN customer AS c USING (customer_id) "
					+ "JOIN address AS a USING (address_id) "
					+ "JOIN inventory AS i USING (inventory_id) "
					+ "JOIN film AS f USING (film_id) "
					+ "WHERE r.return_date IS NULL "
					+ "AND r.rental_date + CAST(f.rental_duration||' DAYS' AS INTERVAL) < NOW()");
			
			PreparedStatement statement = connection.prepareStatement(query);
						
			ResultSet resultSet = statement.executeQuery();	
			while (resultSet.next())
			{
				OverdueRental overdueRental = new OverdueRental();
				
				overdueRental.setCustomer(resultSet.getString("customer"));
				overdueRental.setPhone(resultSet.getString("phone"));
				overdueRental.setTitle(resultSet.getString("title"));
							
				overdueRentals.add(overdueRental);
			}
		} 
		catch (SQLException e)
		{
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new DAOException(e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
				{
					connection.close();
				}
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}
		
		logger.info("Exit: getOverdueRentals");
		logger.info("overdueRentals: " + overdueRentals);
		return overdueRentals;
	}
}
