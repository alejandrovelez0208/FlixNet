package com.flixnet.exception;

public class InvalidCredentialsException extends RuntimeException {

	private static final long serialVersionUID = -7869054450114037095L;

	public InvalidCredentialsException(String message) {
		super(message);
	}

}
