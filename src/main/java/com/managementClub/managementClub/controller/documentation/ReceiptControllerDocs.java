package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name = "Recibos",
        description = "Operaciones relacionadas con los recibos."
)
public interface ReceiptControllerDocs {

    @Operation(
            summary = "Obtener propuestas de recibo",
            description = "Obtener propuestas de recibo para personas que cumplen las condiciones de membresía."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Propuestas de recibo obtenidas correctamente.")
    })
    ResponseEntity<List<ReceiptProposalResponseDTO>> getReceiptProposal();

    @Operation(
            summary = "Genera recibo de una persona a partir de su propuesta",
            description = "Genera recibo de una persona a partir de su propuesta y genera linea de recibo de quota."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Recibo generado correctamente."),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son válidos, o no se puede generar un recibo sin líneas seleccionadas ni cuota incluida, o alguna de las líneas indicadas no está en estado PENDING",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "La persona indicada no existe, o alguna de las líneas de recibo indicadas no existe, no pertenece a la persona",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<ReceiptResponseDTO> generateReceipt(@Valid @RequestBody GenerateReceiptRequestDTO request);

    @Operation(
            summary = "Genera todos los recibos a partir de la propuesta",
            description = "Genera todos los recibos a partir de las propuestas de recibo indicadas. Los fallos individuales (persona inexistente, línea inválida, recibo sin contenido) no interrumpen el proceso: se reportan dentro del resultado, con éxito parcial."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Proceso de generación en lote completado. La respuesta incluye el resultado individual de cada persona (éxito o fallo con motivo), independientemente de si alguna falló."),
            @ApiResponse(responseCode = "400",
                    description = "Los datos enviados no son válidos (por ejemplo, algún campo obligatorio ausente en alguna de las peticiones)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<GenerateReceiptBatchResponseDTO> generateReceiptBatch(@Valid @RequestBody List<@Valid GenerateReceiptRequestDTO> requests);

    @Operation(
            summary = "Obtiene los recibos de una persona",
            description = "Obtiene los recibos de una persona a partir de su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Recibos obtenidos correctamente."),
            @ApiResponse(responseCode = "404",
                    description = "La persona indicada no existe.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByPersonId(@PathVariable Long id);

    @Operation(
            summary = "Obtiene el detalle de un recibo",
            description = "Obtiene el detalle de un recibo con sus lineas de recibo a partir de su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Detalle recibo obtenido correctamente."),
            @ApiResponse(responseCode = "404",
                    description = "La recibo indicado no existe.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<ReceiptDetailResponseDTO> getReceiptDetail(@PathVariable Long id);
}
