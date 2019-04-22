package com.linor.singer.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FileController {
	
	@Value("${myapp.upload_folder:/temp}")
	private String UPLOAD_FOLDER;
	
	@GetMapping("/uploadForm")
	public String uploadForm() {
		return "fileUpload";
	}
	
	@PostMapping("/uploadMyFile")
	public String uploadFile(@RequestParam("myFile") MultipartFile file, ModelMap modelMap) {
		if(!file.isEmpty()) {
			try {
				file.transferTo(new File( UPLOAD_FOLDER + File.separator + file.getOriginalFilename()));
				modelMap.addAttribute("file", file);
				return "viewUploadFile";
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:/uploadForm";
	}
}
