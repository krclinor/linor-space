package com.linor.singer.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 60)
public class JdbcSessionConfig {
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource datasource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("org/springframework/session/jdbc/schema-drop-postgresql.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("org/springframework/session/jdbc/schema-postgresql.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("schema.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("data.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(datasource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }
}
