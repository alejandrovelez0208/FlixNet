package com.flixnet.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VideoRequest {

	@NotBlank(message = "Title is required")
	private String title;

	@Size(max = 4000, message = "Description cannot exceed 4000 characters")
	private String description;

	public String src;
	private String year;
	private String rating;
	private Integer duration;
	private String poster;
	private boolean published;
	private List<String> categories;

}
