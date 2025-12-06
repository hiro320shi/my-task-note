package com.example.mytasknote.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import javax.sql.DataSource;

@Slf4j
@Configuration
@Profile("aws")
public class SecretsDataSourceConfig {

    @Bean
    public DataSource dataSource(Environment env) throws Exception {
        final String region = env.getProperty("aws.region");
        final String secretName = env.getProperty("aws.secrets.dbSecretName");

        if (!StringUtils.hasText(region) || !StringUtils.hasText(secretName)) {
            throw new IllegalStateException("aws.region / aws.secrets.dbSecretName must be set");
        }

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        String secretString = client.getSecretValue(
                GetSecretValueRequest.builder().secretId(secretName).build()
        ).secretString();

        ObjectMapper om = new ObjectMapper();
        JsonNode root = om.readTree(secretString);

        String username = root.path("username").asText();
        String password = root.path("password").asText();
        String host     = root.path("host").asText();
        int    port     = root.path("port").asInt(3306);
        String dbname   = root.path("dbname").asText();

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=true&requireSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8",
                host, port, dbname);

        log.info("Building HikariDataSource for {}", host);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(1);
        ds.setPoolName("hikari-aws");
        return ds;
    }
}
