package com.flixnet.exception;

public class EmailAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -4825875861557938083L;

	public EmailAlreadyExistsException(String message) {
		super(message);
	}

}
