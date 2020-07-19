package com.linor.singer.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	public String storeFile(MultipartFile file);
	public ByteArrayResource loadFileAsResource(String fileName);
}
