package com.linor.singer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(JdbcApplication.class, args);
	}
//
//	@Bean
//	public DataSourceTransactionManager txManager1(DataSource dataSource) {
//		return new DataSourceTransactionManager(dataSource);
//	}
//
}
