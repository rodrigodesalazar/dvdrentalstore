package com.ongres.dvdrentalstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ongres.dvdrentalstore.dto.Actor;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;

@Repository
public class ReportingDAO
{
	@Value("${database.username}")
	private String username;
	
	@Value("${database.password}")
	private String password;
	
	@Value("${database.url}")
	private String url;

	public Integer clientsByCountry(String country, String city)
	{
		Connection connection = null;
		Integer numberOfClients = null;
		try
		{
			StringBuilder query = new StringBuilder(
					"SELECT COUNT(*) AS clients FROM customer AS c "
					+ "JOIN address AS a USING (address_id) "
					+ "JOIN city AS ci USING (city_id) "
					+ "JOIN country AS co USING (country_id) WHERE co.country=?");
			if (city != null)
			{
				query.append(" and ci.city=?");
			}
			
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setString(1, country);
			if (city != null)
			{
				statement.setString(2, city);
			}
			
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			numberOfClients = resultSet.getInt("clients");
			
			connection.close();
		} 
		catch (SQLException e)
		{
			
			e.printStackTrace();
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				
				e.printStackTrace();
			}
		}

		return numberOfClients;
	}
	
	public List<Film> filmsByActor(String actor)
	{
		Connection connection = null;
		List<Film> filmsByActor = new ArrayList<Film>();
		
		try
		{
			StringBuilder query = new StringBuilder(
					"SELECT a.first_name AS firstname, a.last_name AS lastname, f.title AS title, f.description AS description, c.name AS categoryname "
					+ "FROM actor AS a "
					+ "JOIN film_actor AS fa USING (actor_id) "
					+ "JOIN film AS f USING (film_id) "
					+ "JOIN film_category AS fc USING (film_id) "
					+ "JOIN category AS c USING (category_id) "
					+ "WHERE a.first_name LIKE ? OR a.last_name LIKE ? OR a.first_name||' '||a.last_name LIKE ?");
			
			StringBuilder actorWildcard = new StringBuilder("%" + actor.toUpperCase() + "%");
			
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
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
			
			connection.close();
		} 
		catch (SQLException e)
		{
			
			e.printStackTrace();
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				
				e.printStackTrace();
			}
		}

		return filmsByActor;
	}

	public List<OverdueRental> overdueRentals()
	{
		Connection connection = null;
		List<OverdueRental> overdueRentals = new ArrayList<OverdueRental>();
		
		try
		{
			StringBuilder query = new StringBuilder(
					"SELECT c.last_name||', '||c.first_name AS customer, a.phone AS phone, f.title AS title "
					+ "FROM rental AS r "
					+ "JOIN customer AS c USING (customer_id) "
					+ "JOIN address AS a USING (address_id) "
					+ "JOIN inventory AS i USING (inventory_id) "
					+ "JOIN film AS f USING (film_id) "
					+ "WHERE r.return_date IS NULL "
					+ "AND r.rental_date + CAST(f.rental_duration||' DAYS' AS INTERVAL) < NOW()");
			
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
						
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next())
			{
				OverdueRental overdueRental = new OverdueRental();
				
				overdueRental.setCustomer(resultSet.getString("customer"));
				overdueRental.setPhone(resultSet.getString("phone"));
				overdueRental.setTitle(resultSet.getString("title"));
							
				overdueRentals.add(overdueRental);
			}
			
			connection.close();
		} 
		catch (SQLException e)
		{
			
			e.printStackTrace();
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				
				e.printStackTrace();
			}
		}

		return overdueRentals;
	}
}
