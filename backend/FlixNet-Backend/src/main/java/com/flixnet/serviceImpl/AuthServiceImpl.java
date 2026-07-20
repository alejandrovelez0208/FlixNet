package com.flixnet.serviceImpl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.flixnet.dao.UserRepository;
import com.flixnet.dto.request.UserRequest;
import com.flixnet.dto.response.EmailValidationResponse;
import com.flixnet.dto.response.LoginResponse;
import com.flixnet.dto.response.MessageResponse;
import com.flixnet.entity.User;
import com.flixnet.enums.Role;
import com.flixnet.exception.AccountDeactivatedException;
import com.flixnet.exception.BadCredentialsException;
import com.flixnet.exception.EmailAlreadyExistsException;
import com.flixnet.exception.EmailNotVerifiedException;
import com.flixnet.exception.InvalidCredentialsException;
import com.flixnet.exception.InvalidTokenException;
import com.flixnet.security.JwtUtil;
import com.flixnet.service.AuthService;
import com.flixnet.service.EmailService;
import com.flixnet.util.ServiceUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

	private final EmailService emailService;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil;

	private final ServiceUtils serviceUtils;

	@Override
	public MessageResponse signup(@RequestBody UserRequest userRequest) {
		if (userRepository.existsByEmail(userRequest.getEmail())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}

		User user = new User();
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setFullName(userRequest.getFullName());
		user.setRole(Role.USER);
		user.setIsActive(true);
		user.setEmailVerified(false);
		String verificationToken = UUID.randomUUID().toString();
		user.setVerificationToken(verificationToken);
		user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

		userRepository.save(user);
		emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);

		return new MessageResponse("Registration succesful! please check your email to verify your account");
	}

	@Override
	public LoginResponse login(String email, String password) {
		User user = userRepository.findByEmail(email).filter(u -> passwordEncoder.matches(password, u.getPassword()))
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		if (!user.getIsActive()) {
			throw new AccountDeactivatedException(
					"Your account has been deactivated. Please contanct support for assistance");
		}

		if (!user.isEmailVerified()) {
			throw new EmailNotVerifiedException(
					"This verify your email address before loggin in. Check your inbox for the verification link");
		}

		final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

		return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
	}

	@Override
	public EmailValidationResponse validateEmail(String email) {
		boolean exits = userRepository.existsByEmail(email);
		return new EmailValidationResponse(exits, !exits);
	}

	@Override
	public MessageResponse verifyEmail(String token) {
		User user = userRepository.findByVerificationToken(token)
				.orElseThrow(() -> new InvalidTokenException("Invalid or experired verification token"));
		if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(Instant.now())) {
			throw new InvalidTokenException("Verification link has expired. Please request a new one");
		}
		user.setEmailVerified(true);
		user.setVerificationToken(token);
		user.setVerificationTokenExpiry(null);

		userRepository.save(user);

		return new MessageResponse("Email verified succesfully! you can now login");
	}

	@Override
	public MessageResponse resendVerificationEmail(String email) {
		User user = serviceUtils.getUserByEmailOrThrow(email);

		String verificationToken = UUID.randomUUID().toString();
		user.setVerificationToken(verificationToken);
		user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

		userRepository.save(user);
		emailService.sendVerificationEmail(email, verificationToken);

		return new MessageResponse("Verification email resent succesfully! please check your inbox");
	}

	@Override
	public MessageResponse forgotPassword(String email) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		String resetToken = UUID.randomUUID().toString();
		user.setPasswordResetToken(resetToken);
		user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));

		userRepository.save(user);
		emailService.sendPasswordResetEmail(email, resetToken);

		return new MessageResponse("Password reset email sent succesfully! please check your inbox");
	}

	@Override
	public MessageResponse resetPassword(String token, String newPassword) {
		User user = userRepository.findByPasswordResetToken(token)
				.orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));

		if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
			throw new InvalidTokenException("Reset token has expired");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setPasswordResetToken(null);
		user.setPasswordResetTokenExpiry(null);

		userRepository.save(user);

		return new MessageResponse("Password reset successfully. You can now log in with your new password");
	}

	@Override
	public MessageResponse changePassword(String email, String currentPassword, String newPassword) {
		User user = serviceUtils.getUserByEmailOrThrow(email);

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new InvalidCredentialsException("Current password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		return new MessageResponse("Password changed successfully");
	}

	@Override
	public LoginResponse currentUser(String email) {
		User user = serviceUtils.getUserByEmailOrThrow(email);
		return new LoginResponse(null, user.getEmail(), user.getFullName(), user.getRole().name());
	}
}
