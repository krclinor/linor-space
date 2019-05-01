package com.linor.app.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
@EnableJpaRepositories(
		entityManagerFactoryRef = "entityManagerFactory2",
		transactionManagerRef = "transactionManager2",
		basePackages = {"com.linor.app.db2.repository"})
public class Datasource2Config {
	@Autowired
	Environment env;

	@Bean(name = "dataSource2")
	@ConfigurationProperties("db.db2.datasource")
	public DataSource dataSource2() {
		return DataSourceBuilder.create().build();
	}

	private Map<String, ?> hibernateProperties(Environment env, String prefix) {
		
		Map<String, Object> hibernateProp = new HashMap<>();
		hibernateProp.put("hibernate.dialect", env.getProperty(prefix + ".hibernate.dialect", "org.hibernate.dialect.H2Dialect"));
		hibernateProp.put("hibernate.hbm2ddl.auto", env.getProperty(prefix + ".hibernate.hbm2ddl.auto", "none"));
		hibernateProp.put("hibernate.format_sql", env.getProperty(prefix + ".hibernate.format_sql", "true"));
		hibernateProp.put("hibernate.use_sql_comments", env.getProperty(prefix + ".hibernate.use_sql_comments", "true"));
		hibernateProp.put("hibernate.show_sql", env.getProperty(prefix + ".hibernate.show_sql", "false"));
		hibernateProp.put("hibernate.physical_naming_strategy", env.getProperty(prefix + ".hibernate.physical-naming-strategy", 
				"com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy"));
		hibernateProp.put("hibernate.max_fetch_depth", env.getProperty(prefix + ".hibernate.max_fetch_depth","3"));
		hibernateProp.put("hibernate.jdbc.batch_size", env.getProperty(prefix + ".hibernate.jdbc.batch_size","10"));
		hibernateProp.put("hibernate.jdbc.fetch_size", env.getProperty(prefix + ".hibernate.jdbc.fetch_size", "50"));
		hibernateProp.put("hibernate.jdbc.lob.non_contextual_creation", env.getProperty(prefix + ".hibernate.jdbc.lob.non_contextual_creation", "false"));
		hibernateProp.put("hibernate.temp.use_jdbc_metadata_default", env.getProperty(prefix + ".hibernate.temp.use_jdbc_metadata_default", "false"));
		return hibernateProp;
	}
	
	@Bean(name = "entityManagerFactory2")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory2(
			EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource2") DataSource dataSource) {

		Map<String, ?> jpaProperties = hibernateProperties(env, "db2.jpa");

		return builder
				.dataSource(dataSource)
				.packages("com.linor.app.db2.domain")
				.persistenceUnit("db2")
				.properties(jpaProperties)
				.build();
	}
	
	@Bean(name = "transactionManager2")
	public PlatformTransactionManager transactionManager2(
			@Qualifier("entityManagerFactory2") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

    @Bean
    public DataSourceInitializer dataSourceInitializer2(@Qualifier("dataSource2") DataSource datasource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("schema-post2.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("data-post2.sql"));

            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(datasource);
            dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
            return dataSourceInitializer;
    }
}
