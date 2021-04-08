package com.tmb.oneapp.productsexpservice.config;


import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Enabling Swagger for Api
 */
@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${swagger.host:localhost:8080}")
    private String swaggerHost;

    @Value("${spring.application.description}")
    private String appDescription;

    /**
     * Swagger api information method
     */
    private ApiInfo apiEndPointsInfo() {
        return new ApiInfo(appName, appDescription, "1.0", "", new Contact("TCS Team", "", ""), "", "",
                Collections.emptyList());
    }

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2).host(swaggerHost).select()
                .apis(RequestHandlerSelectors.basePackage("com.tmb.oneapp.productsexpservice.controller"))
                .paths(PathSelectors.any()).build().apiInfo(apiEndPointsInfo());
    }
}