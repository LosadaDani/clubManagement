package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(
        description = "Información resumida de un recibo propuesto."
)
public class ReceiptProposalResponseDTO {
    @Schema(
            description = "Información resumida de la persona."
    )
    private PersonSummaryDTO person;
    @Schema(
            description = "Lista de lineas pendientes."
    )
    private List<ReceiptLineResponseDTO> pendingLines;
    @Schema(
            description = "Propuesta de cuota.",
            example = "100.00"
    )
    private BigDecimal proposedQuota; //nullable
    @Schema(
            description = "Concepto de cuota.",
            example = "Cuota de membresía"
    )
    private String quotaConcept; //nullable

    public ReceiptProposalResponseDTO() {
    }

    public ReceiptProposalResponseDTO(PersonSummaryDTO person, List<ReceiptLineResponseDTO> pendingLines, BigDecimal proposedQuota, String quotaConcept) {
        this.person = person;
        this.pendingLines = pendingLines;
        this.proposedQuota = proposedQuota;
        this.quotaConcept = quotaConcept;
    }

    public PersonSummaryDTO getPerson() {
        return person;
    }

    public void setPerson(PersonSummaryDTO person) {
        this.person = person;
    }

    public List<ReceiptLineResponseDTO> getPendingLines() {
        return pendingLines;
    }

    public void setPendingLines(List<ReceiptLineResponseDTO> pendingLines) {
        this.pendingLines = pendingLines;
    }

    public BigDecimal getProposedQuota() {
        return proposedQuota;
    }

    public void setProposedQuota(BigDecimal proposedQuota) {
        this.proposedQuota = proposedQuota;
    }

    public String getQuotaConcept() {
        return quotaConcept;
    }

    public void setQuotaConcept(String quotaConcept) {
        this.quotaConcept = quotaConcept;
    }
}
