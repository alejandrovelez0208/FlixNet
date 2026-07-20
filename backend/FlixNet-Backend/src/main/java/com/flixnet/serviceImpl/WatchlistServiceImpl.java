package com.flixnet.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.flixnet.dao.UserRepository;
import com.flixnet.dao.VideoRepository;
import com.flixnet.dto.response.MessageResponse;
import com.flixnet.dto.response.PageResponse;
import com.flixnet.dto.response.VideoResponse;
import com.flixnet.entity.User;
import com.flixnet.entity.Video;
import com.flixnet.service.WatchlistService;
import com.flixnet.util.PaginationUtils;
import com.flixnet.util.ServiceUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

	private final UserRepository userRepository;

	private final VideoRepository videoRepository;

	private final ServiceUtils serviceUtils;

	@Override
	public MessageResponse addToWatchlist(String email, Long videoId) {
		User user = serviceUtils.getUserByEmailOrThrow(email);

		Video video = serviceUtils.getVideoByIdOrThrow(videoId);

		user.addToWatchlist(video);
		userRepository.save(user);

		return new MessageResponse("Video added to watchlist successfully");
	}

	@Override
	public MessageResponse removeFromWatchlist(String email, Long videoId) {
		User user = serviceUtils.getUserByEmailOrThrow(email);

		Video video = serviceUtils.getVideoByIdOrThrow(videoId);

		user.removeFromWatchlist(video);
		userRepository.save(user);

		return new MessageResponse("Video removed from watchlist successfully");
	}

	@Override
	public PageResponse<VideoResponse> getWatchlistPaginated(String email, int page, int size, String search) {
		User user = serviceUtils.getUserByEmailOrThrow(email);

		Pageable pageable = PaginationUtils.createPageRequest(page, size);
		Page<Video> videoPage;

		if (search != null && !search.trim().isEmpty()) {
			videoPage = userRepository.searchWatchlistByUserId(user.getId(), search.trim(), pageable);
		} else {
			videoPage = userRepository.findWatchlistByUserId(user.getId(), pageable);
		}
		return PaginationUtils.toPageResponse(videoPage, VideoResponse::fromEntity);
	}

}
