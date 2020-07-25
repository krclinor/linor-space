package com.linor.singer.adapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class FileTransformer {
	public String transform(String filePath) throws IOException{
		String content = new String(Files.readAllBytes(Paths.get(filePath)));
		return "변환된 내용 : " + content;
	}
}
