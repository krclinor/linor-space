package com.linor.app.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.linor.app.model.Message;
import com.linor.app.model.OutputMessage;

@Controller
public class WebSocketController {
	
	@MessageMapping("/chatting")
	@SendTo("/topic/messages")
	public OutputMessage send(Message message) throws Exception{
		String time = new SimpleDateFormat("HH:mm").format(new Date());
		return new OutputMessage(message.getFrom(), message.getText(), time);
	}
}
