package com.tmb.oneapp.productsexpservice.config;

import com.mongodb.ConnectionString;
import com.tmb.common.logger.TMBLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
public class PrimaryMongoConfig {

    private static final TMBLogger<PrimaryMongoConfig> logger = new TMBLogger<>(PrimaryMongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Bean
    public MongoClientFactoryBean mongoClientFactoryBean() {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        String uri = mongodbUri;

        logger.info("initial mongodb connection : {}", uri);

        if (!mongodbUri.contains("@")) {
            uri = mongodbUri.replace("mongodb://", (new StringBuilder("mongodb://")).append(username).append(":").append(password).append("@").toString());
        }

        ConnectionString connectionString = new ConnectionString(uri);
        factoryBean.setConnectionString(connectionString);
        return factoryBean;
    }

}