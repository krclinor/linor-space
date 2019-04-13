package com.linor.singer.adapter;

import java.util.Random;

import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer implements MessageSource<String> {

	private String produce() {
		String[] array = {"첫 번째 줄!", "두 번째 줄!", "세 번째 줄!"};
		return array[new Random().nextInt(3)];
	}

	@Override
	public Message<String> receive() {
		return new GenericMessage<String>(this.produce());
	}
}