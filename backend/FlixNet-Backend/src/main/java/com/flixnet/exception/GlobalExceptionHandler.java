package com.flixnet.exception;

import java.time.Instant;
import java.util.Map;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(BadCredentialsException ex) {
		log.warn("BadCredentialException: {}", ex.getMessage());
		return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(AccountDeactivatedException.class)
	public ResponseEntity<Map<String, Object>> handleAccountDeactived(AccountDeactivatedException ex) {
		log.warn("AccountDeactivatedException: {}", ex.getMessage());
		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(EmailNotVerifiedException.class)
	public ResponseEntity<Map<String, Object>> handleEmailNotVerified(EmailNotVerifiedException ex) {
		log.warn("EmailNotVerifiedException: {}", ex.getMessage());
		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(EmailSendingException.class)
	public ResponseEntity<Map<String, Object>> handleEmailSendingException(EmailSendingException ex) {
		log.warn("EmailSendingException: {}", ex.getMessage());
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
		log.warn("InvalidCredentialsException: {}", ex.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
		log.warn("ResourceNotFoundException: {}", ex.getMessage());
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidToken(InvalidTokenException ex) {
		log.warn("InvalidTokenException: {}", ex.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(InvalidRoleException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidRoleException(InvalidRoleException ex) {
		log.warn("InvalidRoleException: {}", ex.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleAlreadyExistsException(EmailAlreadyExistsException ex) {
		log.warn("EmailAlreadyExistsException: {}", ex.getMessage());
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
		String mensaje = ex.getBindingResult().getFieldErrors().stream().findFirst()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).orElse("Validation Request");

		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now(), "error", mensaje));
	}

	@ExceptionHandler({ AsyncRequestNotUsableException.class, ClientAbortException.class })
	public void handleClientAbort(Exception ex) {
		log.debug("Client closed connection during streaming (expected for video seeking/bufferig) : {}",
				ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
		log.warn("Exception: {}", ex.getMessage());
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
		Map<String, Object> body = Map.of("timestamp", Instant.now(), "error", message);
		return ResponseEntity.status(status).body(body);
	}
}
