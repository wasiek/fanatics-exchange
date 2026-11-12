package com.github.dziki.fanatics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fanaticsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fanatics Exchange Platform API")
                        .description("High-availability trading exchange platform. " +
                                "Manages trading accounts, orders, and trade execution with real-time balance updates.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dziki")
                                .email("dziki@example.com"))
                        .license(new License()
                                .name("MIT")));
    }
}
