package com.flixnet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoStatsResponse {

	private Long totalVideos;
	private Long publishedVideoss;
	private Long totalDuration;
}
