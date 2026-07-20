package com.flixnet.service;

import com.flixnet.dto.request.UserRequest;
import com.flixnet.dto.response.MessageResponse;
import com.flixnet.dto.response.PageResponse;
import com.flixnet.dto.response.UserResponse;

public interface UserService {

	MessageResponse createUser(UserRequest userRequest);

	MessageResponse updateUser(Long id, UserRequest userRequest);

	PageResponse<UserResponse> getUsers(int page, int size, String search);

	MessageResponse deleteUser(Long id, String currentUserEmail);

	MessageResponse toggleUserStatus(Long id, String currentUserEmail);

	MessageResponse changeUserRole(Long id, UserRequest userRequest);

}
