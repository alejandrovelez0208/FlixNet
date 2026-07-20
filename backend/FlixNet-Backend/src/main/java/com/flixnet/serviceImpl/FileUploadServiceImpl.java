package com.flixnet.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.flixnet.service.FileUploadService;
import com.flixnet.util.FileHandleUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

	private Path videoStoreLocation;

	private Path imageStoreLocation;

	@Value("${file.upload.video-dir:uploads/videos}")
	private String videoDir;

	@Value("${file.upload.image-dir:uploads/images}")
	private String imageDir;

	@PostConstruct
	public void init() {
		this.videoStoreLocation = Path.of(videoDir).toAbsolutePath().normalize();
		this.imageStoreLocation = Path.of(imageDir).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.videoStoreLocation);
			Files.createDirectories(this.imageStoreLocation);
		} catch (Exception e) {
			throw new RuntimeException("Could not create the directory where the upload fileas will be stored.", e);
		}
	}

	@Override
	public String storeVideoFile(MultipartFile file) {
		return storeFile(file, videoStoreLocation);
	}

	private String storeFile(MultipartFile file, Path storeLocation) {
		String fileExtension = FileHandleUtil.extractFileExtention(file.getOriginalFilename());
		String uuid = UUID.randomUUID().toString();
		String fileName = uuid + fileExtension;

		try {
			if (file.isEmpty()) {
				throw new RuntimeException("Failed to store empty file " + fileName);
			}

			Path targetLocation = storeLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return uuid;
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file " + fileName, e);
		}
	}

	@Override
	public String storeImageFile(MultipartFile file) {
		return storeFile(file, imageStoreLocation);
	}

	@Override
	public ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader, String tokenParam) {
		try {
			Path filePath = FileHandleUtil.findFileByUui(videoStoreLocation, uuid);
			Resource resource = FileHandleUtil.createFullResource(filePath);

			String fileName = filePath.getFileName().toString();
			String contentType = FileHandleUtil.detectVideoContentType(fileName);
			long fileLenght = resource.contentLength();

			if (isFullContentRequest(rangeHeader)) {
				return buildFullVideoResponse(resource, contentType, fileName, fileLenght);
			}

			return buildPartialVideoResponse(filePath, rangeHeader, contentType, fileName, fileLenght);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	private ResponseEntity<Resource> buildPartialVideoResponse(Path filePath, String rangeHeader, String contentType,
			String fileName, long fileLenght) throws IOException {
		long[] range = FileHandleUtil.parseRangeHeader(rangeHeader, fileLenght);
		long rangeStart = range[0];
		long rangeEnd = range[1];

		if (!isValidRange(rangeStart, rangeEnd, fileLenght)) {
			return buildRangeNotSatisfiableResponse(fileLenght);
		}

		long contentLength = rangeEnd - rangeStart + 1;

		Resource rangeResource = FileHandleUtil.createRangeResource(filePath, rangeStart, contentLength);

		return ResponseEntity.status(206).contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
				.header(HttpHeaders.ACCEPT_RANGES, "bytes")
				.header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLenght)
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength)).body(rangeResource);
	}

	private ResponseEntity<Resource> buildRangeNotSatisfiableResponse(long fileLenght) {
		return ResponseEntity.status(416).header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLenght).build();
	}

	private boolean isValidRange(long rangeStart, long rangeEnd, long fileLenght) {
		return rangeStart <= rangeEnd && rangeStart >= 0 && rangeEnd < fileLenght;
	}

	private ResponseEntity<Resource> buildFullVideoResponse(Resource resource, String contentType, String fileName,
			long fileLenght) {
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
				.header(HttpHeaders.ACCEPT_RANGES, "bytes")
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLenght)).body(resource);
	}

	private boolean isFullContentRequest(String rangeHeader) {
		return rangeHeader == null || rangeHeader.isEmpty();
	}

	@Override
	public ResponseEntity<Resource> serveImage(String uuid) {
		try {
			Path filePath = FileHandleUtil.findFileByUui(imageStoreLocation, uuid);
			Resource resource = FileHandleUtil.createFullResource(filePath);

			String fileName = filePath.getFileName().toString();
			String contentType = FileHandleUtil.detectImageContentType(fileName);

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
					.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength())).body(resource);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

}
