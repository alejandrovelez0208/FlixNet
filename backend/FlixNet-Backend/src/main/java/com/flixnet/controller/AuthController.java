package com.flixnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flixnet.dto.request.ChangePasswordRequest;
import com.flixnet.dto.request.EmailRequest;
import com.flixnet.dto.request.LoginRequest;
import com.flixnet.dto.request.ResetPasswordRequest;
import com.flixnet.dto.request.UserRequest;
import com.flixnet.dto.response.EmailValidationResponse;
import com.flixnet.dto.response.LoginResponse;
import com.flixnet.dto.response.MessageResponse;
import com.flixnet.service.AuthService;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<MessageResponse> register(@Valid @RequestBody UserRequest userRequest) {
		return ResponseEntity.ok(authService.signup(userRequest));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/validate-email")
	public ResponseEntity<EmailValidationResponse> validateEmail(@RequestParam String email) {
		return ResponseEntity.ok(authService.validateEmail(email));
	}

	@GetMapping("/verify-email")
	public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
		return ResponseEntity.ok(authService.verifyEmail(token));
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody EmailRequest emailRequest) {
		return ResponseEntity.ok(authService.resendVerificationEmail(emailRequest.getEmail()));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody EmailRequest emailRequest) {
		return ResponseEntity.ok(authService.forgotPassword(emailRequest.getEmail()));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<MessageResponse> resetPassword(
			@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		return ResponseEntity
				.ok(authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword()));
	}

	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(Authentication authentication,
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

		String email = authentication.getName();

		return ResponseEntity.ok(authService.changePassword(email, changePasswordRequest.getCurrentPassword(),
				changePasswordRequest.getNewPassword()));
	}

	@GetMapping("current-user")
	public ResponseEntity<LoginResponse> currentUser(Authentication authentication) {
		String email = authentication.getName();
		return ResponseEntity.ok(authService.currentUser(email));
	}
}
