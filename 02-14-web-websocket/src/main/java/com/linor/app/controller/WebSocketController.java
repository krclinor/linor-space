package com.linor.app.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import com.linor.app.model.Message;
import com.linor.app.model.OutputMessage;

@RestController
public class WebSocketController {
	
	@MessageMapping("/chatting")
	@SendTo("/topic/messages")
	public OutputMessage send(Message message) throws Exception{
		return OutputMessage.builder()
				.from(message.getFrom())
				.text(message.getText())
				.time(LocalDateTime.now())
				.build();
	}
}
