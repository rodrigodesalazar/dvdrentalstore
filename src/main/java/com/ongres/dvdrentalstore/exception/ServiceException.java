package com.ongres.dvdrentalstore.exception;

public class ServiceException extends Exception 
{
	private static final long serialVersionUID = -1951167096544479213L;

	public ServiceException(String message)
	{
		super(message);
	}
}
