package com.yigit.ecommerce.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Condition;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    static class DatabaseUrlCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String url = System.getenv("DATABASE_URL");
            return url != null && !url.isEmpty();
        }
    }

    @Bean
    @Primary
    @Conditional(DatabaseUrlCondition.class)
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        log.info("=== DataSourceConfig: DATABASE_URL detected, configuring from env ===");
        log.info("=== DATABASE_URL host: {} ===", URI.create(databaseUrl).getHost());

        URI uri = URI.create(databaseUrl);
        String[] userInfo = uri.getUserInfo().split(":", 2);
        String username = userInfo[0];
        String password = userInfo.length > 1 ? userInfo[1] : "";
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s",
                uri.getHost(), uri.getPort(), uri.getPath());

        log.info("=== JDBC URL: {} ===", jdbcUrl);
        log.info("=== Username: {} ===", username);
        log.info("=== Password length: {} ===", password.length());

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");

        return ds;
    }
}
