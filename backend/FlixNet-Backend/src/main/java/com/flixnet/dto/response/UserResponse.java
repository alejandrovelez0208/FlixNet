package com.flixnet.dto.response;

import java.time.Instant;

import com.flixnet.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	private Long id;
	private String email;
	private String fullName;
	private String role;
	private Boolean active;
	private Instant createdAt;
	private Instant updatedAt;

	public static UserResponse fromEntity(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name(),
				user.getIsActive(), user.getCreatedAt(), user.getUpdatedAt());
	}
}
