package com.flixnet.exception;

public class EmailNotVerifiedException extends RuntimeException {

	private static final long serialVersionUID = -6313700948589994021L;

	public EmailNotVerifiedException(String message) {
		super(message);
	}

}
