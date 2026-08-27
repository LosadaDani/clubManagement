package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.ReceiptStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        description = "Información resumida de un recibo."
)
public class ReceiptSummaryDTO {
    @Schema(
            description = "Identificador único del recibo.",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "Fecha de emisión del recibo.",
            example = "2026-08-27"
    )
    private LocalDate issueDate;
    @Schema(
            description = "Estado del recibo.",
            example = "PAID"
    )
    private ReceiptStatus status;

    public ReceiptSummaryDTO() {
    }

    public ReceiptSummaryDTO(Long id, LocalDate issueDate, ReceiptStatus status) {
        this.id = id;
        this.issueDate = issueDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public ReceiptStatus getStatus() {
        return status;
    }

    public void setStatus(ReceiptStatus status) {
        this.status = status;
    }
}
