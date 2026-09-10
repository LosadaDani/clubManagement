package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.ReceiptStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Representa un recibo ya creado")
public class ReceiptResponseDTO {

    @Schema(description = "Identificador del recibo",
            example = "1")
    private Long id;

    @Schema(description = "Persona a la que pertenece el recibo")
    private PersonSummaryDTO person;

    @Schema(description = "Fecha de emisión del recibo",
            example = "2026-09-10")
    private LocalDate issueDate;

    @Schema(description = "Importe total del recibo",
            example = "40.00")
    private BigDecimal amount;

    @Schema(description = "Estado del recibo",
            example = "ISSUED")
    private ReceiptStatus status;

    public ReceiptResponseDTO() {
    }

    public ReceiptResponseDTO(Long id, PersonSummaryDTO person, LocalDate issueDate, BigDecimal amount, ReceiptStatus status) {
        this.id = id;
        this.person = person;
        this.issueDate = issueDate;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonSummaryDTO getPerson() {
        return person;
    }

    public void setPerson(PersonSummaryDTO person) {
        this.person = person;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ReceiptStatus getStatus() {
        return status;
    }

    public void setStatus(ReceiptStatus status) {
        this.status = status;
    }
}
