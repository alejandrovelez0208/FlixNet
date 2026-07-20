package com.flixnet.exception;

public class BadCredentialsException extends RuntimeException {

	private static final long serialVersionUID = -7010138816354220325L;

	public BadCredentialsException(String message) {
		super(message);
	}

}
