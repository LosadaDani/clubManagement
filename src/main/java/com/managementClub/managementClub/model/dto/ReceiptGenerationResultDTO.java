package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado del recibo al crearse")
public class ReceiptGenerationResultDTO {

    @Schema(description = "Id de la persona para la que se intentó generar el recibo")
    private Long personId;

    @Schema(description = "true si el recibo se generó correctamente")
    private Boolean success;

    @Schema(description = "Recibo generado, solo presente si success = true")
    private ReceiptResponseDTO receipt;

    @Schema(description = "Motivo del fallo, solo presente si success = false")
    private String errorMessage;

    public ReceiptGenerationResultDTO() {
    }

    public ReceiptGenerationResultDTO(Long personId, Boolean success, ReceiptResponseDTO receipt, String errorMessage) {
        this.personId = personId;
        this.success = success;
        this.receipt = receipt;
        this.errorMessage = errorMessage;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ReceiptResponseDTO getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptResponseDTO receipt) {
        this.receipt = receipt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
