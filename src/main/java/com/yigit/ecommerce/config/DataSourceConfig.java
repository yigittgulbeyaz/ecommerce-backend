package com.yigit.ecommerce.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Auto-configures DataSource from DATABASE_URL environment variable.
 * This is the standard pattern for Railway/Heroku PaaS deployments.
 * 
 * DATABASE_URL format: postgresql://user:password@host:port/database
 */
@Configuration
@ConditionalOnProperty(name = "DATABASE_URL")
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            throw new RuntimeException("DATABASE_URL environment variable is not set");
        }

        URI uri = URI.create(databaseUrl);
        String[] userInfo = uri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s",
                uri.getHost(), uri.getPort(), uri.getPath());

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");

        return dataSource;
    }
}
