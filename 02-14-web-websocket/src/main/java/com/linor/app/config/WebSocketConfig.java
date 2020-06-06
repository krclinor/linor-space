package com.linor.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//in-memory message-broker, topic에 대한 prefix 설정
		registry.enableSimpleBroker("/topic");
		
		//메세지를 수신하는 handler의 메세지 prefix 설정 
		registry.setApplicationDestinationPrefixes("/app");
	}
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat");
		
		registry.addEndpoint("/chat")
			.withSockJS();
	}
}
