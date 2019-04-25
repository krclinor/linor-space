package com.linor.app.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@MapperScan(basePackages = {"com.linor.app.db1.dao"}, sqlSessionFactoryRef = "sqlSessionFactory1")
public class Datasource1Config {
	@Bean
	@ConfigurationProperties("db.db1.datasource")
	@Primary
	public DataSource dataSource1() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	@Primary
	public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource1") DataSource dataSource, ApplicationContext applicationContext) throws Exception{
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource);
		sqlSessionFactory.setMapperLocations(applicationContext.getResources("classpath:/**/db1/dao/*.xml"));;
		return sqlSessionFactory.getObject();
	}

    @Bean
    public DataSourceInitializer dataSourceInitializer1(@Qualifier("dataSource1") DataSource datasource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("schema-post1.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("data-post1.sql"));

            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(datasource);
            dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
            return dataSourceInitializer;
    }
}
