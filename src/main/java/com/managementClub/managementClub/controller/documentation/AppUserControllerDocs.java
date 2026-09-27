package com.managementClub.managementClub.controller.documentation;


import com.managementClub.managementClub.model.dto.AppUserCreatedResponseDTO;
import com.managementClub.managementClub.model.dto.AppUserRequestDTO;
import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Usuarios",
        description = "Operaciones para la gestión de usuarios del sistema"
)
public interface AppUserControllerDocs {

    @Operation(
            summary = "Generar un nuevo usuario",
            description = "Genera un nuevo usuario en el sistema apartir de una Persona existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Usuario generado exitosamente"),
            @ApiResponse(responseCode = "400",
                    description = "La persona indicada ya tiene un usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Persona no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AppUserCreatedResponseDTO> generateUser(@Valid AppUserRequestDTO requestDTO);
}
