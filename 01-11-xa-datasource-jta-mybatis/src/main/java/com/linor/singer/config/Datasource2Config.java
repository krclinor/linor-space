package com.linor.singer.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@MapperScan(basePackages = {"com.linor.singer.dao2"}, sqlSessionFactoryRef = "sqlSessionFactory2")
public class Datasource2Config {
	@Bean
	@ConfigurationProperties("db.db2.datasource")
	public DataSource dataSource2() {
		return new AtomikosDataSourceBean();
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory2(@Qualifier("dataSource2") DataSource dataSource, ApplicationContext applicationContext) throws Exception{
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource);
		sqlSessionFactory.setTypeAliasesPackage("com.linor.singer.domain2");
		sqlSessionFactory.setMapperLocations(applicationContext.getResources("classpath*:/**/dao2/*.xml"));;
		
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.setMapUnderscoreToCamelCase(true);
		configuration.setJdbcTypeForNull(JdbcType.NULL);
		sqlSessionFactory.setConfiguration(configuration);

		return sqlSessionFactory.getObject();
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
