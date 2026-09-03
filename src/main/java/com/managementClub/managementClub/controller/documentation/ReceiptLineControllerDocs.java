package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.ErrorResponseDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineRequestDTO;
import com.managementClub.managementClub.model.dto.ReceiptLineResponseDTO;
import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
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
        name = "Línea de recibo",
        description = "Operaciones relacionadas con las líneas de recibo"
)
public interface ReceiptLineControllerDocs {

    @Operation(
            summary = "Crear línea de recibo",
            description = "Crea una nueva línea de recibo"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                description = "Linea de recibo creada exitosamente"),
            @ApiResponse(responseCode = "400",
                description = "Los datos enviados no son validos",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "404",
                description = "No existe la persona indicada",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            )
    })
    ResponseEntity<ReceiptLineResponseDTO> createReceiptLine(@Valid ReceiptLineRequestDTO requestDTO);

    @Operation(
            summary = "Buscar líneas de recibo por persona",
            description = "Busca las líneas de recibo de una persona"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                description = "Listado de las líneas de recibo de la persona encontrado correctamente"),
            @ApiResponse(responseCode = "400",
                description = "El valor indicado para el filtro de estado no es válido",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "404",
                description = "No existe la persona indicada",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            )
    })
    ResponseEntity<List<ReceiptLineResponseDTO>> findByPerson(@Parameter(description = "Id de la persona") Long id, @Parameter(description = "Estado de la línea de recibo") ReceiptLineStatus status);
}
