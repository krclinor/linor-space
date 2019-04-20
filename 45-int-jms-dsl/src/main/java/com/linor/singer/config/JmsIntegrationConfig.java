package com.linor.singer.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.core.JmsTemplate;

import com.linor.singer.adapter.MyMessageConverter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class JmsIntegrationConfig {
	private final MyMessageConverter converter;
	
	@Bean
	public ConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL("tcp://localhost:61616");
		connectionFactory.setUserName("admin");
		connectionFactory.setPassword("admin");
		return connectionFactory;
	}
	
	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory());
		template.setMessageConverter(converter);
		return template;
	}
	
	@Bean
	public IntegrationFlow integrationFlow(JmsTemplate jmsTemplate) {
		return IntegrationFlows.from(Jms.inboundAdapter(jmsTemplate)
					.destination("POSITION_INBOUND"), spec -> spec.poller(Pollers.fixedRate(1000)))
				.handle(Jms.outboundAdapter(jmsTemplate).destination("POSITION_OUTBOUND"))
				.get();
	}
}
