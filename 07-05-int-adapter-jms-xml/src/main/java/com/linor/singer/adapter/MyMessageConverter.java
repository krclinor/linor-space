package com.linor.singer.adapter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		String textData = (String) object;
		log.info("To 메시지 : " + textData);
		textData += "꼬리표 달기";
		TextMessage message = session.createTextMessage(textData);
		return message;
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		log.info("From 메시지 : " + message);
		TextMessage textMessage = (TextMessage)message;
		String messageData = textMessage.getText();
		return messageData;
	}
}
