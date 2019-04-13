package com.linor.singer.config;

import java.io.File;
import java.io.FileWriter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;

import com.linor.singer.adapter.FileTransformer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class FileIntegrationConfig {
	private final FileTransformer transformer;
	
	@Bean
	public FileReadingMessageSource fileReader() {
		FileReadingMessageSource source = new FileReadingMessageSource();
		source.setDirectory(new File("source"));
		return source;
	}
	
	@Bean
	public FileWritingMessageHandler fileWriter() {
		FileWritingMessageHandler handler = new FileWritingMessageHandler(new File("destination"));
		handler.setDeleteSourceFiles(true);
		handler.setExpectReply(false);
		return handler;
	}
	
	@Bean
	public IntegrationFlow integrationFlow() {
		return IntegrationFlows.from(fileReader(), s -> s.poller(Pollers.fixedDelay(5000)))
				.transform(transformer, "transform")
				.handle(fileWriter())
				.get();
	}
}
