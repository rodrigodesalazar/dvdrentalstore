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

import com.ongres.dvdrentalstore.exception.DAOException;

@Repository
public class RentalDAO
{
	private static final Logger logger = LoggerFactory.getLogger(RentalDAO.class);
	
	@Value("${database.username}")
	private String username;

	@Value("${database.password}")
	private String password;

	@Value("${database.url}")
	private String url;

	public List<Integer> getInventoryIDs(Integer customerID, String title) throws DAOException
	{
		logger.info("Enter: getInventoryIDs");
		logger.info("customerID: " + customerID);
		logger.info("title: " + title);
		
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
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new DAOException(e.getMessage());
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}

		logger.info("Exit: getInventoryIDs");
		logger.info("inventoryIDs: " + inventoryIDs);
		return inventoryIDs;
	}
	
	public Boolean isAvailable(Integer inventoryID) throws DAOException
	{
		logger.info("Enter: isAvailable");
		logger.info("inventoryID: " + inventoryID);
		
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
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new DAOException(e.getMessage());
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}

		logger.info("Exit: isAvailable");
		logger.info("available: " + available);
		return available;
	}
	
	public Integer registerRental(Integer customerID, String staffName, Integer inventoryID) throws DAOException
	{
		logger.info("Enter: registerRental");
		logger.info("customerID: " + customerID);
		logger.info("staffName: " + staffName);
		logger.info("inventoryID: " + inventoryID);
		
		Connection connection = null;
		Integer rentalID = null;
		
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			
			Integer staffID = getStaffID(connection, customerID, staffName);
			
			StringBuilder query = new StringBuilder(
					"INSERT INTO rental "
					+ "(rental_date, customer_id, staff_id, inventory_id) "
					+ "VALUES (NOW(), ?, ?, ?) RETURNING rental_id AS rentalid");
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setInt(1, customerID);
			statement.setInt(2, staffID);
			statement.setInt(3, inventoryID);
						
			ResultSet resultSet = statement.executeQuery();
			
			resultSet.next();
			
			rentalID = resultSet.getInt("rentalid");
			
			connection.close();
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
				connection.close();
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}

		logger.info("Exit: registerRental");
		logger.info("rentalID: " + rentalID);
		return rentalID;
	}
	
	// Assuming unique name per store
	private Integer getStaffID(Connection connection, Integer customerID, String staffName) throws SQLException
	{
		logger.info("Enter: getStaffID");
		logger.info("customerID: " + customerID);
		logger.info("staffName: " + staffName);
		
		StringBuilder rentalIDQuery = new StringBuilder(
				"SELECT sta.staff_id AS staffid FROM staff AS sta "
				+ "JOIN store AS sto USING (store_id) "
				+ "JOIN customer as c USING (store_id) "
				+ "WHERE c.customer_id=? AND sta.first_name||' '||sta.last_name=?");
										
		PreparedStatement statement = connection.prepareStatement(rentalIDQuery.toString());
		statement.setInt(1, customerID);
		statement.setString(2, staffName);
			
		ResultSet resultSet = statement.executeQuery();
		resultSet.next();
		Integer staffID = resultSet.getInt("staffid");
		
		logger.info("Exit: getStaffID");
		logger.info("staffID: " + staffID);
		return staffID;
	}

	public Double getCustomerBalance(Integer customerID) throws DAOException
	{
		logger.info("Enter: getCustomerBalance");
		logger.info("customerID: " + customerID);
		
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
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new DAOException(e.getMessage());
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}

		logger.info("Exit: getCustomerBalance");
		logger.info("balance: " + balance);
		return balance;
	}

	public void registerPayment(Integer customerID, String staffName, Integer rentalID, Double customerBalance) throws DAOException
	{
		logger.info("Enter: registerPayment");
		logger.info("customerID: " + customerID);
		logger.info("staffName: " + staffName);
		logger.info("rentalID: " + rentalID);
		logger.info("customerBalance: " + customerBalance);
		
		Connection connection = null;
				
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			
			Integer staffID = getStaffID(connection, customerID, staffName);
			
			StringBuilder query = new StringBuilder(
					"INSERT INTO payment "
					+ "(customer_id, staff_id, rental_id, amount, payment_date) "
					+ "VALUES (?, ?, ?, ?, NOW())");
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setInt(1, customerID);
			statement.setInt(2, staffID);
			statement.setInt(3, rentalID);
			statement.setDouble(4, customerBalance);
						
			Integer rowsUpdated = statement.executeUpdate();
			
			logger.info("rowsUpdated: " + rowsUpdated);
			
			connection.close();
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
				connection.close();
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}
		logger.info("Exit: registerPayment");
	}

	// Assuming title=title_id
	public void updateRental(Integer customerID, String title) throws DAOException
	{
		logger.info("Enter: updateRental");
		logger.info("customerID: " + customerID);
		logger.info("title: " + title);
		
		Connection connection = null;
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			
			Integer rentalID = getRentalID(connection, customerID, title);
			
			StringBuilder query = new StringBuilder(
					"UPDATE rental "
					+ " SET return_date=NOW() "
					+ " WHERE rental_id=?");
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setInt(1, rentalID);
			
			Integer rowsUpdated = statement.executeUpdate();
			
			logger.info("rowsUpdated: " + rowsUpdated);
			
			connection.close();
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
				connection.close();
			}
			catch (SQLException e)
			{
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new DAOException(e.getMessage());
			}
		}
		logger.info("Exit: updateRental");
	}
	
	private Integer getRentalID(Connection connection, Integer customerID, String title) throws SQLException
	{
		logger.info("Enter: getRentalID");
		logger.info("customerID: " + customerID);
		logger.info("title: " + title);
		
		StringBuilder query = new StringBuilder(
				"SELECT r.rental_id AS rentalid FROM rental AS r "
				+ "JOIN inventory AS i USING (inventory_id) "
				+ "JOIN film AS f USING (film_id) "
				+ "WHERE r.customer_id=? AND f.title=?");
											
		PreparedStatement statement = connection.prepareStatement(query.toString());
		statement.setInt(1, customerID);
		statement.setString(2, title);
		
		ResultSet resultSet = statement.executeQuery();
		resultSet.next();
		Integer rentalID = resultSet.getInt("rentalid");
		
		logger.info("Exit: getRentalID");
		logger.info("rentalID: " + rentalID);
		return rentalID;
	}
}
