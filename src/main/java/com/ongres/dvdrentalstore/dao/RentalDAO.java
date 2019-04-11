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

/**
 * This class deals with database access related to renting DVDs.
 * 
 * @author rodrigodesalazar
 *
 */
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
	
	@Value("${error.notRented}")
	private String notRented;

	/**
	 * Returns a list of the inventory ids of the copies of a certain film.
	 * There can be several copies at a store.
	 * 
	 * @param customerID the id of the customer.
	 * @param title the title of the film.
	 * @return a list of the inventory ids of the film.
	 * @throws DAOException if anything goes wrong.
	 */
	public List<Integer> getInventoryIDs(Integer customerID, String title) throws DAOException
	{
		logger.info("Enter: getInventoryIDs");
		logger.info("customerID: " + customerID);
		logger.info("title: " + title);
		
		Connection connection = null;
		List<Integer> inventoryIDs = new ArrayList<Integer>();
		
		try
		{
			String query = new String(
					"SELECT i.inventory_id AS inventoryid FROM inventory as i "
					+ "JOIN store AS s USING (store_id) "
					+ "JOIN customer as c USING (store_id) "
					+ "JOIN film AS f USING (film_id) "
					+ "WHERE c.customer_id=? AND f.title=?");
								
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, customerID);
			statement.setString(2, title);
						
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next())
			{			
				inventoryIDs.add(resultSet.getInt("inventoryid"));
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

		logger.info("Exit: getInventoryIDs");
		logger.info("inventoryIDs: " + inventoryIDs);
		return inventoryIDs;
	}
	
	/**
	 * Checks whether a certain copy of a film is available or not.
	 * 
	 * @param inventoryID the inventory id of the film.
	 * @return true if the copy is available, false otherwise.
	 * @throws DAOException if anything goes wrong.
	 */
	public Boolean isAvailable(Integer inventoryID) throws DAOException
	{
		logger.info("Enter: isAvailable");
		logger.info("inventoryID: " + inventoryID);
		
		Connection connection = null;
		Boolean available = null;
		
		try
		{
			String query = new String(
					"SELECT inventory_in_stock(?) AS available");
								
			connection = DriverManager.getConnection(url, username, password);
			
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, inventoryID);
						
			ResultSet resultSet = statement.executeQuery();
			
			resultSet.next();
			
			available = resultSet.getBoolean("available");
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

		logger.info("Exit: isAvailable");
		logger.info("available: " + available);
		return available;
	}
	
	/**
	 * Make the insertions corresponding to the rental of a DVD.
	 * The fact that we are modifying two different tables in the same DAO
	 * method could be interpreted as a violation of the single responsibility principle.
	 * 
	 * @param customerID the id of the customer.
	 * @param staffName name of the store clerk serving the customer. 
	 * @param inventoryID the inventory id of the film.
	 * @throws DAOException if anything goes wrong.
	 */
	public void registerRentalAndPayment(Integer customerID, String staffName, Integer inventoryID) throws DAOException
	{
		logger.info("Enter: registerRentalAndPayment");
		logger.info("customerID: " + customerID);
		logger.info("staffName: " + staffName);
		logger.info("inventoryID: " + inventoryID);
		
		Connection connection = null;
		
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(false);
			
			Integer staffID = getStaffID(connection, customerID, staffName);
			
			Integer rentalID = registerRental(connection, customerID, staffID, inventoryID);

			Double customerBalance = getCustomerBalance(connection, customerID);

			registerPayment(connection, customerID, staffID, rentalID, customerBalance);
			
			connection.commit();
		} 
		catch (SQLException e)
		{
			try
			{
				if (connection != null)
				{
					connection.rollback();
				}
			}
			catch (SQLException f)
			{
				logger.error(ExceptionUtils.getStackTrace(f));
				throw new DAOException(f.getMessage());
			}
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

		logger.info("Exit: registerRentalAndPayment");
	}
	
	/**
	 * Returns the staff id of a store clerk.
	 * We assume that there are no two clerks with the same name in the same store,
	 * as that would return several results and we would just get the first one,
	 * we couldn't decide which one it is with the input we have. 
	 * Besides, I checked and there are no duplicates ;)
	 * 
	 * @param connection database connection.
	 * @param customerID the id of the customer.
	 * @param staffName name of the store clerk serving the customer. 
	 * @return the staff id of the clerk.
	 * @throws SQLException if anything goes wrong.
	 */
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
	
	/**
	 * Inserts a record in the rental table.
	 * 
	 * @param connection database connection.
	 * @param customerID the id of the customer.
	 * @param staffID the staff id of the clerk. 
	 * @param inventoryID the inventory id of the film.
	 * @return the rental id of the insert.
	 * @throws SQLException if anything goes wrong.
	 */
	private Integer registerRental(Connection connection, Integer customerID, Integer staffID, Integer inventoryID) throws SQLException
	{
		logger.info("Enter: registerRental");
		logger.info("customerID: " + customerID);
		logger.info("staffID: " + staffID);
		logger.info("inventoryID: " + inventoryID);
			
		String query = new String(
				"INSERT INTO rental "
				+ "(rental_date, customer_id, staff_id, inventory_id) "
				+ "VALUES (NOW(), ?, ?, ?) RETURNING rental_id AS rentalid");
			
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, customerID);
		statement.setInt(2, staffID);
		statement.setInt(3, inventoryID);
						
		ResultSet resultSet = statement.executeQuery();
		
		resultSet.next();
			
		Integer rentalID = resultSet.getInt("rentalid");
			
		logger.info("Exit: registerRental");
		logger.info("rentalID: " + rentalID);
		return rentalID;
	}

	/**
	 * Returns the balance of a customer.
	 * 
	 * @param connection database connection.
	 * @param customerID the id of the customer.
	 * @return balance of the customer.
	 * @throws SQLException if anything goes wrong.
	 */
	private Double getCustomerBalance(Connection connection, Integer customerID) throws SQLException
	{
		logger.info("Enter: getCustomerBalance");
		logger.info("customerID: " + customerID);
		
		String query = new String(
				"SELECT get_customer_balance(?, LOCALTIMESTAMP) AS balance");
								
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, customerID);
						
		ResultSet resultSet = statement.executeQuery();
			
		resultSet.next();
			
		Double balance = resultSet.getDouble("balance");
			
		logger.info("Exit: getCustomerBalance");
		logger.info("balance: " + balance);
		return balance;
	}

	/**
	 * Inserts a record in the payment table.
	 * 
	 * @param connection database connection.
	 * @param customerID the id of the customer.
	 * @param staffID the staff id of the clerk. 
	 * @param rentalID the rental id.
	 * @param customerBalance balance of the customer.
	 * @throws SQLException if anything goes wrong.
	 */
	private void registerPayment(Connection connection, Integer customerID, Integer staffID, Integer rentalID, Double customerBalance) throws SQLException
	{
		logger.info("Enter: registerPayment");
		logger.info("customerID: " + customerID);
		logger.info("staffID: " + staffID);
		logger.info("rentalID: " + rentalID);
		logger.info("customerBalance: " + customerBalance);
			
		String query = new String(
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
			
		logger.info("Exit: registerPayment");
	}

	/**
	 * Update the rental record with the return date of the DVD.
	 * We assume that the titles are unique,
	 * we can't decide which film we would be returning otherwise.
	 * I also checked for this on the database :P
	 * 
	 * @param customerID the id of the customer.
	 * @param title the title of the film.
	 * @throws DAOException if anything goes wrong.
	 */
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
		logger.info("Exit: updateRental");
	}
	
	/**
	 * Return the rental id of the film.
	 * We can't know which copy we are returning if we have rented several.
	 * Will return the first one found.
	 * 
	 * @param connection database connection.
	 * @param customerID the id of the customer.
	 * @param title the title of the film.
	 * @return the rental id.
	 * @throws SQLException if anything goes wrong.
	 * @throws DAOException 
	 */
	private Integer getRentalID(Connection connection, Integer customerID, String title) throws SQLException, DAOException
	{
		logger.info("Enter: getRentalID");
		logger.info("customerID: " + customerID);
		logger.info("title: " + title);
		
		StringBuilder query = new StringBuilder(
				"SELECT r.rental_id AS rentalid FROM rental AS r "
				+ "JOIN inventory AS i USING (inventory_id) "
				+ "JOIN film AS f USING (film_id) "
				+ "WHERE r.customer_id=? AND f.title=? AND return_date IS NULL");
											
		PreparedStatement statement = connection.prepareStatement(query.toString());
		statement.setInt(1, customerID);
		statement.setString(2, title);
		
		ResultSet resultSet = statement.executeQuery();
		if (!resultSet.next())
		{
			logger.error(notRented);
			throw new DAOException(notRented);
		}
		Integer rentalID = resultSet.getInt("rentalid");
		
		logger.info("Exit: getRentalID");
		logger.info("rentalID: " + rentalID);
		return rentalID;
	}
}
