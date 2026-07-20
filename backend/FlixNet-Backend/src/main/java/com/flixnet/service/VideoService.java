package com.flixnet.service;

import java.util.List;

import com.flixnet.dto.request.VideoRequest;
import com.flixnet.dto.response.MessageResponse;
import com.flixnet.dto.response.PageResponse;
import com.flixnet.dto.response.VideoResponse;
import com.flixnet.dto.response.VideoStatsResponse;

import jakarta.validation.Valid;

public interface VideoService {

	MessageResponse createVideoByAdmin(@Valid VideoRequest videoRequest);

	PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search);

	MessageResponse updateVideoByAdmin(Long id, @Valid VideoRequest videoRequest);

	MessageResponse deleteVideoByAdmin(Long id);

	MessageResponse toggleVideoPublishStatusByAdmin(Long id, boolean value);

	VideoStatsResponse getAdminStats();

	PageResponse<VideoResponse> getPublishedVideos(int page, int size, String search, String email);

	List<VideoResponse> getFeaturedVideso();

}
