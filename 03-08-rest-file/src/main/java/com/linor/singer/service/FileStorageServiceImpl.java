package com.linor.singer.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.linor.singer.exception.FileStorageException;
import com.linor.singer.exception.MyFileNotFoundException;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	private Path fileStorageLocation;
	
	public FileStorageServiceImpl(@Value("${myapp.upload-dir:test}") String uploadDir) {
		this.fileStorageLocation = Paths.get(uploadDir)
				.toAbsolutePath()
				.normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		}catch (Exception e) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
		}
	}
	
	@Override
	public String storeFile(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		if(!file.isEmpty()) {
			try {
				Path targetLocation = this.fileStorageLocation.resolve(fileName);
	            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			}catch(IOException e) {
				throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
			}
		}
		return fileName;
	}
	
	@Override
	public ByteArrayResource loadFileAsResource(String fileName){
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			byte[] data = Files.readAllBytes(filePath);
			ByteArrayResource resource = new ByteArrayResource(data);
			if(resource.exists()) {
				return resource;
			}else {
				throw new MyFileNotFoundException("File not found" + fileName);
			}
		}catch(MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found" + fileName);
		}catch(IOException e) {
			throw new FileStorageException("Could not retrieve file " + fileName + ". Please try again!", e);
		}
	}
}
