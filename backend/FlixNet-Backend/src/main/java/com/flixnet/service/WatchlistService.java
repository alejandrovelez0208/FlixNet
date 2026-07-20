package com.flixnet.service;

import com.flixnet.dto.response.MessageResponse;
import com.flixnet.dto.response.PageResponse;
import com.flixnet.dto.response.VideoResponse;

public interface WatchlistService {

	MessageResponse addToWatchlist(String email, Long videoId);

	MessageResponse removeFromWatchlist(String email, Long videoId);

	PageResponse<VideoResponse> getWatchlistPaginated(String email, int page, int size, String search);

}
