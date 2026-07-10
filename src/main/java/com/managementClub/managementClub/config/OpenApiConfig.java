package com.managementClub.managementClub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Management Club API",
                version = "1.0",
                description = "API para la gestión de Clubs deportivos Caninos"
        )
)
public class OpenApiConfig {
}
