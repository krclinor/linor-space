package com.linor.singer.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FileController {
	private final ServletContext servletContext;
	private MediaType getMediaTypeForFileName(String fileName) {
		String mimeType = servletContext.getMimeType(fileName);
		try {
			MediaType mediaType = MediaType.parseMediaType(mimeType);
			return mediaType;
		}catch(Exception e) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}
	
	@Value("${myapp.download_folder:/test}")
	private String DOWNLOAD_FOLDER;
	
	private final String FILE_NAME = "test.txt";
	
	/**
	 * ResponseEntity<InputStreamResource>를 이용한 방법
	 * http://localhost:8080/download1?fileName=abc.zip
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/download1")
	public ResponseEntity<InputStreamResource> downloadFile1(
			@RequestParam(defaultValue = FILE_NAME) String fileName) throws IOException{
		MediaType mediaType = getMediaTypeForFileName(fileName);
		File file = new File(DOWNLOAD_FOLDER + File.separator + fileName);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		//한글파일명을 위한 설정
		String headerfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		
		return ResponseEntity.ok()
				// Content-Disposition
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachement;filename=" + headerfileName)
				// Content-Type
				.contentType(mediaType)
				// Content-Length
				.contentLength(file.length())
				.body(resource);
	}
	
	/**
	 * ResponseEntity<ByteArrayResource>를 이용한 방법
	 * http://localhost:8080/download2?fileName=abc.zip
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/download2")
	public ResponseEntity<ByteArrayResource> downloadFile2(
			@RequestParam(defaultValue = FILE_NAME) String fileName) throws IOException{
		MediaType mediaType = getMediaTypeForFileName(fileName);
		Path path = Paths.get(DOWNLOAD_FOLDER + File.separator + fileName);
		byte[] data = Files.readAllBytes(path);
		ByteArrayResource resource = new ByteArrayResource(data);
		//한글파일명을 위한 설정
		String headerfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		
		return ResponseEntity.ok()
				// Content-Disposition
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachement;filename=" + headerfileName)
				// Content-Type
				.contentType(mediaType)
				// Content-Length
				.contentLength(data.length)
				.body(resource);
	}
	
	/**
	 * HttpServletResponse를 이용한 방법
	 * http://localhost:8080/download3?fileName=abc.zip
	 * @param response
	 * @param fileName
	 * @throws IOException
	 */
	@RequestMapping("/download3")
	public void downloadFile3(HttpServletResponse response, 
			@RequestParam(defaultValue = FILE_NAME) String fileName) throws IOException{
		MediaType mediaType = getMediaTypeForFileName(fileName);
		File file = new File(DOWNLOAD_FOLDER + File.separator + fileName);
		//한글파일명을 위한 설정
		String headerfileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		response.setContentType(mediaType.getType());
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + headerfileName);
		response.setContentLength((int)file.length());
		
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		while((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();
	}
}
