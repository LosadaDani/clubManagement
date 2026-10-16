package com.managementClub.managementClub.config;

import com.managementClub.managementClub.controller.AuthController;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Management Club API",
                version = "1.0",
                description = "API para la gestión de Clubs deportivos Caninos"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

        @Bean
        public OperationCustomizer standardSecurityResponse(){
                return (operation, handlerMethod) -> {
                        if (AuthController.class.isAssignableFrom(handlerMethod.getBeanType())) {
                                operation.setSecurity(List.of());
                                return operation;
                        }
                        operation.getResponses().addApiResponse("401", errorResponse("No autenticado: falta el token o no es válido."));
                        operation.getResponses().addApiResponse("403", errorResponse("Sin permiso: el rol no permite la operación, o el recurso no pertenece al usuario."));
                        return operation;
                };
        }

        private ApiResponse errorResponse(String description) {
                return new ApiResponse()
                        .description(description)
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(
                                        new Schema<>().$ref("#/components/schemas/ErrorResponseDTO"))));
        }
}
