package com.flixnet.exception;

public class InvalidTokenException extends RuntimeException {

	private static final long serialVersionUID = 532989866511480087L;

	public InvalidTokenException(String message) {
		super(message);
	}

}
