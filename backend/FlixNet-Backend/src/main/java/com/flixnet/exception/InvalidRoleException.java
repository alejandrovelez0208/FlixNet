package com.flixnet.exception;

public class InvalidRoleException extends RuntimeException {

	private static final long serialVersionUID = -6546076024856785081L;

	public InvalidRoleException(String message) {
		super(message);
	}

}
