package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado de la generacion por lote de los recibos")
public class GenerateReceiptBatchResponseDTO {

    @Schema(description = "Resultados individuales, uno por cada persona solicitada")
    private List<ReceiptGenerationResultDTO> results;

    @Schema(description = "Número de recibos generados correctamente")
    private int successCount;

    @Schema(description = "Número de fallos")
    private int failureCount;

    public GenerateReceiptBatchResponseDTO() {
    }

    public GenerateReceiptBatchResponseDTO(List<ReceiptGenerationResultDTO> results, int successCount, int failureCount) {
        this.results = results;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public List<ReceiptGenerationResultDTO> getResults() {
        return results;
    }

    public void setResults(List<ReceiptGenerationResultDTO> results) {
        this.results = results;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
}
