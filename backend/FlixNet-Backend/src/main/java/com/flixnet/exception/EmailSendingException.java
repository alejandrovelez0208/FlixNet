package com.flixnet.exception;

public class EmailSendingException extends RuntimeException {

	private static final long serialVersionUID = 308169452501396591L;

	public EmailSendingException(String message, Throwable cause) {
		super(message, cause);
	}

}
