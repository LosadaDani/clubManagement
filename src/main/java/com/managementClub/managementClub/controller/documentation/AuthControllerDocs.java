package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import com.managementClub.managementClub.model.dto.LoginRequestDTO;
import com.managementClub.managementClub.model.dto.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication",
    description = "Endpoints de autenticación de usuarios")
public interface AuthControllerDocs {

    @Operation(
            summary = "Iniciar Sesión",
            description = "Valida las credenciales de un usuario y, si son correctas, devuelve sus datos de sesión.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                description = "Inicio de sesión correcto"),
            @ApiResponse(responseCode = "401",
                description = "Credenciales inválidas",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<LoginResponseDTO> login(@Valid LoginRequestDTO requestDto);
}
