package com.hotel.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Configuração do OpenAPI/Swagger para documentação da API.
 * Aplicando princípios de Clean Code: configuração centralizada e bem documentada.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Backend API")
                        .description("API para gerenciamento de hóspedes e check-ins de hotel. " +
                                "Implementa CRUDL para hóspedes, funcionalidades de check-in/checkout " +
                                "com cálculo automático de valores baseado em regras de negócio específicas.")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Vinicius Eduardo Da Silva")
                                .url("mailto:viniciuseduardo0702@hotmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub")
                        .url("https://github.com/Vinicius-E"));
    }
}