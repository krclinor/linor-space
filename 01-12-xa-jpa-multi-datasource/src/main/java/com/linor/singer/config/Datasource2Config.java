package com.linor.singer.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy;


@Configuration
@EnableTransactionManagement
//@EnableJpaRepositories(
//		entityManagerFactoryRef = "entityManagerFactory2",
//		transactionManagerRef = "transactionManager2",
//		basePackages = {"com.linor.singer.db2.repository"})
public class Datasource2Config {
	@Bean
	@ConfigurationProperties("db.db2.datasource")
	public DataSource dataSource2() {
		return new AtomikosDataSourceBean();
	}
	
	@Bean(name = "entityManagerFactory2")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory2(
			EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource2") DataSource dataSource) {

		Map<String, ?> jpaProperties = hibernateProperties();

		return builder
				.dataSource(dataSource)
				.jta(true)
				.packages("com.linor.singer.domain2")
				.persistenceUnit("db2")
				.properties(jpaProperties)
				.build();
	}

	private Map<String, ?> hibernateProperties() {
		
		Map<String, Object> hibernateProp = new HashMap<>();
		hibernateProp.put("hibernate.dialect", PostgreSQL10Dialect.class.getName());
		hibernateProp.put("hibernate.hbm2ddl.auto", "create");
		hibernateProp.put("hibernate.format_sql", "false");
		hibernateProp.put("hibernate.use_sql_comments", "false");
		hibernateProp.put("hibernate.show_sql", "true");
		hibernateProp.put("hibernate.physical_naming_strategy", CamelCaseToSnakeCaseNamingStrategy.class.getName());
		hibernateProp.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
		hibernateProp.put("javax.persistence.transactionType", "JTA");
		
		return hibernateProp;
	}

//	@Bean(name = "transactionManager2")
//	public PlatformTransactionManager transactionManager2(
//			@Qualifier("entityManagerFactory2") EntityManagerFactory entityManagerFactory) {
//		return new JpaTransactionManager(entityManagerFactory);
//	}

//    @Bean
//    public DataSourceInitializer dataSourceInitializer2(@Qualifier("dataSource2") DataSource datasource) {
//        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
//        resourceDatabasePopulator.addScript(new ClassPathResource("schema-post2.sql"));
//        resourceDatabasePopulator.addScript(new ClassPathResource("data-post2.sql"));
//
//            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
//            dataSourceInitializer.setDataSource(datasource);
//            dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
//            return dataSourceInitializer;
//    }
}
