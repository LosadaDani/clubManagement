package com.managementClub.managementClub.controller.documentation;

import com.managementClub.managementClub.model.dto.ReceiptProposalResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
}
