package com.flixnet.serviceImpl;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flixnet.dao.UserRepository;
import com.flixnet.dto.request.UserRequest;
import com.flixnet.dto.response.MessageResponse;
import com.flixnet.dto.response.PageResponse;
import com.flixnet.dto.response.UserResponse;
import com.flixnet.entity.User;
import com.flixnet.enums.Role;
import com.flixnet.exception.EmailAlreadyExistsException;
import com.flixnet.exception.InvalidRoleException;
import com.flixnet.service.EmailService;
import com.flixnet.service.UserService;
import com.flixnet.util.PaginationUtils;
import com.flixnet.util.ServiceUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final ServiceUtils serviceUtils;

	private final EmailService emailService;

	@Override
	public MessageResponse createUser(UserRequest userRequest) {
		if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
			throw new EmailAlreadyExistsException("Email already exits");
		}

		validateRole(userRequest.getRole());

		User user = new User();
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setFullName(userRequest.getFullName());
		user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
		user.setIsActive(true);
		String verificationToken = UUID.randomUUID().toString();
		user.setVerificationToken(verificationToken);
		user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

		userRepository.save(user);
		emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);

		return new MessageResponse("User created succesfully.");
	}

	private void validateRole(String role) {
		if (Arrays.stream(Role.values()).noneMatch(r -> r.name().equalsIgnoreCase(role))) {
			throw new InvalidRoleException("Invalid role: " + role);
		}
	}

	@Override
	public MessageResponse updateUser(Long id, UserRequest userRequest) {
		User user = serviceUtils.getUserByIdOrThrow(id);

		ensureNotLastActiveAdmin(user);
		validateRole(userRequest.getRole());

		user.setFullName(userRequest.getFullName());
		user.setRole(Role.valueOf(userRequest.getRole()));

		userRepository.save(user);

		return new MessageResponse("User updated succesfully");
	}

	private void ensureNotLastActiveAdmin(User user) {
		if (user.getIsActive() && user.getRole() == Role.ADMIN) {
			Long activeAdminCount = userRepository.countByRoleAndIsActive(Role.ADMIN, true);

			if (activeAdminCount <= 1) {
				throw new RuntimeException("Cannot deactived the last active admin user");
			}
		}
	}

	@Override
	public PageResponse<UserResponse> getUsers(int page, int size, String search) {
		Pageable pageAble = PaginationUtils.createPageRequest(page, size, "id");

		Page<User> userPage;

		if (search != null && !search.trim().isEmpty()) {
			userPage = userRepository.searchUsers(search.trim(), pageAble);
		} else {
			userPage = userRepository.findAll(pageAble);
		}

		return PaginationUtils.toPageResponse(userPage, UserResponse::fromEntity);
	}

	@Override
	public MessageResponse deleteUser(Long id, String currentUserEmail) {
		User user = serviceUtils.getUserByIdOrThrow(id);

		if (user.getEmail().equals(currentUserEmail)) {
			throw new RuntimeException("You cannot delete you own account");
		}

		ensureNotLastActiveAdmin(user, "delete");

		userRepository.deleteById(id);

		return new MessageResponse("User deleted succesfully.");
	}

	private void ensureNotLastActiveAdmin(User user, String operation) {
		if (user.getRole() == Role.ADMIN) {
			long adminCount = userRepository.countByRole(Role.ADMIN);
			if (adminCount <= 1) {
				throw new RuntimeException("Cannot " + operation + " the last admin user");
			}
		}
	}

	@Override
	public MessageResponse toggleUserStatus(Long id, String currentUserEmail) {
		User user = serviceUtils.getUserByIdOrThrow(id);

		if (user.getEmail().equals(currentUserEmail)) {
			throw new RuntimeException("you cannot deactivated your own account");
		}

		ensureNotLastActiveAdmin(user);

		user.setIsActive(!user.getIsActive());

		userRepository.save(user);

		return new MessageResponse("User status updated succesfully");
	}

	@Override
	public MessageResponse changeUserRole(Long id, UserRequest userRequest) {
		User user = serviceUtils.getUserByIdOrThrow(id);

		validateRole(userRequest.getRole());

		Role newRole = Role.valueOf(userRequest.getRole().toUpperCase());
		if (user.getRole() == Role.ADMIN && newRole == Role.USER) {
			ensureNotLastActiveAdmin(user, "change the role of");
		}

		user.setRole(newRole);

		userRepository.save(user);

		return new MessageResponse("User role updated succesfully.");
	}

}
