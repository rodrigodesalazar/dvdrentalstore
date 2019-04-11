package com.ongres.dvdrentalstore.exception;

public class DAORuntimeException extends RuntimeException 
{
	private static final long serialVersionUID = -986698570047821256L;

	public DAORuntimeException(String message)
	{
		super(message);
	}
}
