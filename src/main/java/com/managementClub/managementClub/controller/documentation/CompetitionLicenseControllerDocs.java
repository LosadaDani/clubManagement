package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
    ResponseEntity<CompetitionLicenseResponseDTO> createCompetitionLicense(@Valid CompetitionLicenseRequestDTO requestDTO);

    @Operation(
            summary = "Obtener licencias de competición por perro",
            description = "Obtiene todas las licencias de competición para un perro específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Listado de licencias obtenido correctamente. Puede devolver una lista vacía si el perro no tiene licencias registradas."),
            @ApiResponse(responseCode = "404",
                    description = "No existe ningún perro con el identificador indicado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<List<CompetitionLicenseResponseDTO>> getCompetitionLicensesByDogId(@Parameter(description = "Id del perro")Long dogId);

    @Operation(
            summary = "Obtener licencias de competicion vigentes por perro",
            description = "Obtiene todas las licencias de competición vigentes para un perro específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Listado de licencias vigentes obtenido correctamente. Puede devolver una lista vacía si el perro no tiene licencias vigentes."),
            @ApiResponse(responseCode = "404",
                    description = "No existe ningún perro con el identificador indicado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )

            )
    })
    ResponseEntity<List<CompetitionLicenseResponseDTO>> getLicenseCurrentByDogId(Long dogId);

    @Operation(
            summary = "Actualizar licencia de competición",
            description = "Actualiza los datos de una licencia de competición existente para corregir información incorrecta. No se utiliza para renovar una licencia. Se comprobará que no exista otra licencia para la misma organización, persona y perro con un periodo de vigencia solapado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Licencia de competición actualizada exitosamente"),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son validos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "No existe la licencia, organización, persona o perro indicados",
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
    ResponseEntity<CompetitionLicenseResponseDTO> updateCompetitionLicense(@Parameter(description = "Identificador único de la licencia de competición") Long id, @Valid CompetitionLicenseRequestDTO requestDTO);
}
