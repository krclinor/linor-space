package com.linor.singer.config;

import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.linor.singer.adapter.MessageConsumer;
import com.linor.singer.adapter.MessageProducer;

@Configuration
@EnableIntegration
public class IntegrationFlowConfig {
//	@Bean
//	public MessageSource<String> source(){
//		return new MessageSource<String>() {
//			@Override
//			public Message<String> receive() {
//				String[] array = {"첫 번째 줄!", "두 번째 줄!", "세 번째 줄!"};
//				return new GenericMessage<String>(array[new Random().nextInt(3)]);
//			}
//		};
//	}

	@Bean
	public IntegrationFlow inOutFlow(MessageProducer messageProducer, MessageConsumer messgeConsumer) {
//		return IntegrationFlows.from(source(), s -> s.poller(Pollers.fixedDelay(1000)))
//				.channel("channel")
//				.handle(messgeConsumer, "consume")
//				.get();

		return IntegrationFlows.from(messageProducer, s -> s.poller(Pollers.fixedDelay(1000)))
				.channel("channel")
				.handle(messgeConsumer, "consume")
				.get();
	}
}
