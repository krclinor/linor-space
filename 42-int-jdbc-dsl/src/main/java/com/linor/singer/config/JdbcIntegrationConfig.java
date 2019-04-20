package com.linor.singer.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jdbc.JdbcMessageHandler;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.messaging.MessageHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class JdbcIntegrationConfig {
	private final DataSource dataSource;
	
	@Bean
	public MessageSource<?> fromDb(){
		final String SQL = "select * from item_source where polled = false";
		JdbcPollingChannelAdapter adapter = new JdbcPollingChannelAdapter(dataSource, SQL);
		adapter.setUpdateSql("update item_source set polled = true where item_id in (:item_id)");
		return adapter;
	}
	
	@Bean
	public MessageHandler toDb() {
		final String SQL = "insert into item_dest(item_id, description) values (:payload[item_id], :payload[description])";
		JdbcMessageHandler handler = new JdbcMessageHandler(dataSource, SQL);
		return handler;
	}
	
	@Bean
	public IntegrationFlow jdbcFlow() throws Exception {
		return IntegrationFlows.from(fromDb(), p -> p.poller(Pollers.fixedRate(4000)))
				.channel("channel")
				.handle(toDb())
				.get();
	}
}
