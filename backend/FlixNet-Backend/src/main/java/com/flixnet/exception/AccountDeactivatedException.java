package com.flixnet.exception;

public class AccountDeactivatedException extends RuntimeException {

	private static final long serialVersionUID = 706435185544338436L;

	public AccountDeactivatedException(String message) {
		super(message);
	}

}
