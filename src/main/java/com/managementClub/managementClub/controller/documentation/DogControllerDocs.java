package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Perros",
        description = "Operaciones para la gestión de perros asociados a los miembros del club"
)
public interface DogControllerDocs {

    @Operation(
            summary = "Crear un perro",
            description = "Registra un nuevo perro asociado a una persona existente en el club"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Perro registrado correctamente"
            ),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son válidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "No existe la persona propietaria indicada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "409",
                    description = "Ya existe un perro con el mismo microchip y número de pedigree",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<DogResponseDTO> createDog(DogRequestDTO requestDto);

}