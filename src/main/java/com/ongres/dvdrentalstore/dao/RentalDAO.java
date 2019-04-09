package com.ongres.dvdrentalstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.rest.DVDRentalController;

@Repository
public class RentalDAO
{
	@Value("${database.username}")
	private String username;

	@Value("${database.password}")
	private String password;

	@Value("${database.url}")
	private String url;
	
	private static final Logger logger = LoggerFactory.getLogger(RentalDAO.class);

	public List<Integer> getInventoryIDs(Integer customerID, String title)
	{
		Connection connection = null;
		List<Integer> inventoryIDs = new ArrayList<Integer>();
		
		try
		{
			StringBuilder query = new StringBuilder(
					"SELECT i.inventory_id AS inventoryid FROM inventory as i "
					+ "JOIN store AS s USING (store_id) "
					+ "JOIN customer as c USING (store_id) "
					+ "JOIN film AS f USING (film_id) "
					+ "WHERE c.customer_id=? AND f.title=?");
								
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setInt(1, customerID);
			statement.setString(2, title);
						
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next())
			{			
				inventoryIDs.add(resultSet.getInt("inventoryid"));
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

		return inventoryIDs;
	}
	
	public Boolean isAvailable(Integer inventoryID)
	{
		logger.info("Pasamos");
		Connection connection = null;
		Boolean available = null;
		
		try
		{
			StringBuilder query = new StringBuilder(
					"SELECT inventory_in_stock(?) AS available");
								
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setInt(1, inventoryID);
						
			ResultSet resultSet = statement.executeQuery();
			
			resultSet.next();
			
			available = resultSet.getBoolean("available");
			
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

		return available;
	}

	public Integer getStaffID(String staffName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer rentDVD(Integer customerID, String staffName, Integer inventoryID)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Double getCustomerBalance(Integer customerID)
	{
		Connection connection = null;
		Double balance = null;
		
		try
		{
			StringBuilder query = new StringBuilder(
					"SELECT get_customer_balance(?, LOCALTIMESTAMP) AS balance");
								
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setInt(1, customerID);
						
			ResultSet resultSet = statement.executeQuery();
			
			resultSet.next();
			
			balance = resultSet.getDouble("balance");
			
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

		return balance;
	}

	public void registerBalance(Integer customerID, String staffName, Integer rentalID, Double customerBalance)
	{
		// TODO Auto-generated method stub

	}

	// Assuming title=title_id
	public void returnDVD(Integer customerID, String title)
	{
		Connection connection = null;
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			
			StringBuilder rentalIDQuery = new StringBuilder(
					"SELECT r.rental_id AS rentalid FROM rental AS r "
					+ "JOIN inventory AS i USING (inventory_id) "
					+ "JOIN film AS f USING (film_id) "
					+ "WHERE r.customer_id=? AND f.title=?");
												
			PreparedStatement statement = connection.prepareStatement(rentalIDQuery.toString());
			statement.setInt(1, customerID);
			statement.setString(2, title);
			
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			Integer rentalID = resultSet.getInt("rentalid");
			
			StringBuilder query = new StringBuilder(
					"UPDATE rental "
					+ " SET return_date=NOW() "
					+ " WHERE rental_id=?");
			
			statement = connection.prepareStatement(query.toString());
			statement.setInt(1, rentalID);
			
			Integer rowsUpdated = statement.executeUpdate();
			
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
	}
}
