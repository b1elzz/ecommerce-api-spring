package com.empresa.projeto.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSchemas("Problem", new Schema<Problem>()))
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0")
                        .description("Documentação dos endpoints"))
                .externalDocs(new ExternalDocumentation()
                        .description("Wiki Completa"));
    }
}