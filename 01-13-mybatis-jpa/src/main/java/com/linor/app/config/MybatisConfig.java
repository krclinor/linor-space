package com.linor.app.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(
		basePackages = {"com.linor.app.mybatis.dao"}, 
		sqlSessionFactoryRef = "sqlSessionFactory2")
public class MybatisConfig {
	@Bean
	public SqlSessionFactory sqlSessionFactory2(DataSource dataSource, ApplicationContext applicationContext) throws Exception{
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		
		factoryBean.setDataSource(dataSource);
		factoryBean.setTypeAliasesPackage("com.linor.app.mybatis.domain");
		factoryBean.setTransactionFactory(new ManagedTransactionFactory());
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:/**/mybatis/dao/*.xml"));

		SqlSessionFactory factory = factoryBean.getObject();
		factory.getConfiguration().setMapUnderscoreToCamelCase(true);
		return factory;
	}
}
