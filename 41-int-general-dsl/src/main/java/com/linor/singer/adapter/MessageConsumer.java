package com.linor.singer.adapter;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageConsumer {
	public void consume(String message) {
		log.info("결과 메시지: " + message);
	}
}
