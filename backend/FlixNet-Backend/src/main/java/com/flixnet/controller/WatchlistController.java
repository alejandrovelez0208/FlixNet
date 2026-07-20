package com.flixnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flixnet.dto.response.MessageResponse;
import com.flixnet.dto.response.PageResponse;
import com.flixnet.dto.response.VideoResponse;
import com.flixnet.service.WatchlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

	private final WatchlistService watchlistService;

	@PostMapping("/{videoId}")
	public ResponseEntity<MessageResponse> addToWatchlist(@PathVariable Long videoId, Authentication authentication) {
		String email = authentication.getName();
		return ResponseEntity.ok(watchlistService.addToWatchlist(email, videoId));
	}

	@DeleteMapping("/{videoId}")
	public ResponseEntity<MessageResponse> removeFromWathlist(@PathVariable Long videoId,
			Authentication authentication) {
		String email = authentication.getName();
		return ResponseEntity.ok(watchlistService.removeFromWatchlist(email, videoId));
	}

	@GetMapping
	public ResponseEntity<PageResponse<VideoResponse>> getWatchlist(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
			Authentication authentication) {

		String email = authentication.getName();

		PageResponse<VideoResponse> response = watchlistService.getWatchlistPaginated(email, page, size, search);

		return ResponseEntity.ok(response);
	}
}
