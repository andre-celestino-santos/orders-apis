package com.andre.orders_apis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Orders APIs")
                        .version("1.0.0")
                        .description("Manage Orders")
                        .contact(new Contact()
                                .name("André Celestino dos Santos")
                                .email("andre.celestino.santos@gmail.com")
                        )
                );
    }

}