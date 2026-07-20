package com.flixnet.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.flixnet.service.FileUploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class FileUploadController {

	private final FileUploadService fileUploadService;

	@PostMapping("/upload/video")
	public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam MultipartFile file) {
		String uuid = fileUploadService.storeVideoFile(file);
		return ResponseEntity.ok(buildUploadResponse(uuid, file));
	}

	@PostMapping("/upload/image")
	public ResponseEntity<Map<String, String>> uploadImage(@RequestParam MultipartFile file) {
		String uuid = fileUploadService.storeImageFile(file);
		return ResponseEntity.ok(buildUploadResponse(uuid, file));
	}

	private Map<String, String> buildUploadResponse(String uuid, MultipartFile file) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("uuid", uuid);
		response.put("fileName", file.getOriginalFilename());
		response.put("size", String.valueOf(file.getSize()));
		return response;
	}

	@GetMapping("/video/{uuid}")
	public ResponseEntity<Resource> serveVideo(@PathVariable String uuid,
			@RequestHeader(value = "Range", required = false) String rangeHeader,
			@RequestHeader(value = "token", required = false) String tokenParam) {

		return fileUploadService.serveVideo(uuid, rangeHeader, tokenParam);
	}

	@GetMapping("/image/{uuid}")
	public ResponseEntity<Resource> serveImage(@PathVariable String uuid) {
		return fileUploadService.serveImage(uuid);
	}
}
