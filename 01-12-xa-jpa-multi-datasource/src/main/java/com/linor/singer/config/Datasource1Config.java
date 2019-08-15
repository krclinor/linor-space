package com.linor.singer.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
//@EnableJpaRepositories(
//		entityManagerFactoryRef = "entityManagerFactory",
//		transactionManagerRef = "transactionManager",
//		basePackages = {"com.linor.singer.db1.repository"})
public class Datasource1Config {
	@Bean
	@ConfigurationProperties("db.db1.datasource")
	@Primary
	public DataSource dataSource1() {
		return new AtomikosDataSourceBean();
	}
	
	@Bean(name = "entityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource1") DataSource dataSource) {
		Map<String, ?> jpaProperties = hibernateProperties();
		return builder
				.dataSource(dataSource)
				.jta(true)
				.packages("com.linor.singer.domain1")
				.persistenceUnit("db1")
				.properties(jpaProperties)
				.build();
	}
	private Map<String, ?> hibernateProperties() {
		
		Map<String, Object> hibernateProp = new HashMap<>();
		hibernateProp.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		hibernateProp.put("hibernate.hbm2ddl.auto", "create");
		hibernateProp.put("hibernate.format_sql", "false");
		hibernateProp.put("hibernate.use_sql_comments", "false");
		hibernateProp.put("hibernate.show_sql", "false");
		hibernateProp.put("hibernate.physical_naming_strategy", "com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy");
		hibernateProp.put("hibernate.jdbc.lob.non_contextual_creation", "true");
		
		hibernateProp.put("hibernate.transaction.factory_class", "org.hibernate.transaction.JTATransactionFactory");
		hibernateProp.put("hibernate.transaction.jta.platform", "com.linor.singer.config.AtomikosPlatform");
		hibernateProp.put("hibernate.transaction.coordinator_class", "jta");
		
		return hibernateProp;
	}
	
//	@Primary
//	@Bean(name = "transactionManager")
//	public PlatformTransactionManager transactionManager(
//			@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
//		return new JpaTransactionManager(entityManagerFactory);
//	}

//    @Bean
//    public DataSourceInitializer dataSourceInitializer1(@Qualifier("dataSource1") DataSource datasource) {
//        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
//        resourceDatabasePopulator.addScript(new ClassPathResource("schema-post1.sql"));
//        resourceDatabasePopulator.addScript(new ClassPathResource("data-post1.sql"));
//
//            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
//            dataSourceInitializer.setDataSource(datasource);
//            dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
//            return dataSourceInitializer;
//    }
}
