package com.linor.singer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.endpoint.MethodInvokingMessageSource;

import com.linor.singer.adapter.MessageConsumer;
import com.linor.singer.adapter.MessageProducer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class IntegrationFlowConfig {
	private final MessageProducer messageProducer;
	private final MessageConsumer messgeConsumer;
	
	@Bean
	public MessageSource<?> source(){
		MethodInvokingMessageSource source = new MethodInvokingMessageSource();
		//source.setObject(new MessageProducer());
		source.setObject(messageProducer);
		source.setMethodName("produce");
		return source;
	}

	@Bean
	public IntegrationFlow inOutFlow() {
		return IntegrationFlows.from(source(), s -> s.poller(Pollers.fixedDelay(1000)))
				.channel("channel")
				.handle(messgeConsumer, "consume")
				.get();
	}
}
