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

//	@Autowired
//	private EntityManagerFactory entityManagerFactory;
//
//   @Bean
//   public PlatformTransactionManager transactionManager(){
//      JpaTransactionManager transactionManager
//        = new JpaTransactionManager();
//      transactionManager.setEntityManagerFactory(entityManagerFactory);
//      return transactionManager;
//   }
}
