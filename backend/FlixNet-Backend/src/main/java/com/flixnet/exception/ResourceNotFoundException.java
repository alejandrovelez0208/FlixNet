package com.flixnet.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 588857409298558717L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
