package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import com.managementClub.managementClub.model.dto.OrganizationRequestDTO;
import com.managementClub.managementClub.model.dto.OrganizationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Organización",
        description = "Operaciones relacionadas con las organizaciones"
)
public interface OrganizationControllerDocs {

    @Operation(
            summary = "Crear una organización",
            description = "Registra una nueva organización"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Organización creada exitosamente"),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son validos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "409",
                    description = "Ya existe una organización con el mismo nombre abreviado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<OrganizationResponseDTO> createOrganization(OrganizationRequestDTO requestDto);
}
