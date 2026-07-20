package com.flixnet.service;

import com.flixnet.dto.request.UserRequest;
import com.flixnet.dto.response.EmailValidationResponse;
import com.flixnet.dto.response.LoginResponse;
import com.flixnet.dto.response.MessageResponse;

import jakarta.validation.Valid;

public interface AuthService {

	MessageResponse signup(@Valid UserRequest userRequest);

	LoginResponse login(String email, String password);

	EmailValidationResponse validateEmail(String email);

	MessageResponse verifyEmail(String token);

	MessageResponse resendVerificationEmail(String email);

	MessageResponse forgotPassword(String email);

	MessageResponse resetPassword(String token, String newPassword);

	MessageResponse changePassword(String email, String currentPassword, String newPassword);

	LoginResponse currentUser(String email);
}
