package com.linor.singer.config;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizingMessageSource;
import org.springframework.integration.ftp.outbound.FtpMessageHandler;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
public class FtpIntegrationConfig {
	@Value("${ftp.host:localhost}")
	private String host;
	
	@Value("${ftp.userName:testftp}")
	private String userName;

	@Value("${ftp.password:init00}")
	private String password;
	
	@Bean
	public SessionFactory<FTPFile> ftpFactory(){
		DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
		factory.setHost(host);
		factory.setUsername(userName);
		factory.setPassword(password);
		return new CachingSessionFactory<FTPFile>(factory);
	}
	
	@Bean
	public FtpInboundFileSynchronizer ftpInboundFileSynchronizer() {
		FtpInboundFileSynchronizer ftpSynchronizer = new FtpInboundFileSynchronizer(ftpFactory());
		ftpSynchronizer.setDeleteRemoteFiles(true);
		ftpSynchronizer.setRemoteDirectory("/test1/");
		return ftpSynchronizer;
	}
	
	@Bean
	public MessageSource<File> inFtp(){
		FtpInboundFileSynchronizingMessageSource source = new FtpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizer());
		source.setLocalDirectory(new File("source"));
		return source;
	}

	@Bean
	public MessageHandler ftpHandler() {
		FtpMessageHandler handler = new FtpMessageHandler(ftpFactory());
		handler.setRemoteDirectoryExpressionString("'/test2/'");
		return handler;
	}
	
	@Bean
	public IntegrationFlow integrationFlow() {
		return IntegrationFlows.from(inFtp(), s -> s.poller(Pollers.fixedDelay(4000)))
				.handle(ftpHandler())
				.get();
	}
}
