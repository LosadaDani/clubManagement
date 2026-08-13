package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Licencia de competición",
        description = "Operaciones relacionadas con las licencias de competición")
public interface CompetitionLicenseControllerDocs {

    @Operation(
            summary = "Crear licencia de competición",
            description = "Crea una nueva licencia de competición")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                description = "Licencia de competición creada exitosamente"),
            @ApiResponse(responseCode = "400",
                description = "Los datos enviados no son validos",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "404",
                description = "No existen la organización, persona o perro indicados",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "409",
                description = "Ya existe una licencia para la misma organización, persona y perro con periodo de vigencia solapado",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            )
    })
    ResponseEntity<CompetitionLicenseResponseDTO> createCompetitionLicense(CompetitionLicenseRequestDTO requestDTO);
}
