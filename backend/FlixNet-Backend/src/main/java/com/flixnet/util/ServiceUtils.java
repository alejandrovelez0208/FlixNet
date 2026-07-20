package com.flixnet.util;

import org.springframework.stereotype.Component;

import com.flixnet.dao.UserRepository;
import com.flixnet.dao.VideoRepository;
import com.flixnet.entity.User;
import com.flixnet.entity.Video;
import com.flixnet.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ServiceUtils {

	private final UserRepository userRepository;

	private final VideoRepository videoRepository;

	public User getUserByEmailOrThrow(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email" + email));
	}

	public User getUserByIdOrThrow(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}

	public Video getVideoByIdOrThrow(Long id) {
		return videoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
	}
}
