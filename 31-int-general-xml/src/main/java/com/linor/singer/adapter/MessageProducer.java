package com.linor.singer.adapter;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
	public String produce() {
		String[] array = {"첫 번째 줄!", "두 번째 줄!", "세 번째 줄!"};
		return array[new Random().nextInt(3)];
	}
}