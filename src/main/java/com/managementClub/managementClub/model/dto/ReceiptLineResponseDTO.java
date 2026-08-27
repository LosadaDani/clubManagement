package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        description = "Información completa de una línea de recibo."
)
public class ReceiptLineResponseDTO {

    @Schema(
            description = "Identificador único de la línea de recibo.",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "Persona asociada a la línea de recibo."
    )
    private PersonSummaryDTO person;
    @Schema(
            description = "Fecha de la línea de recibo.",
            example = "2026-08-27"
    )
    private LocalDate date;
    @Schema(
            description = "Concepto o descripción del pago.",
            example = "Cuota mensual agosto 2026"
    )
    private String concept;
    @Schema(
            description = "Importe de la línea de recibo.",
            example = "25.00"
    )
    private BigDecimal amount;
    @Schema(
            description = "Estado de la línea de recibo.",
            example = "PENDING"
    )
    private ReceiptLineStatus status;
    @Schema(
            description = "Recibo al que pertenece esta línea."
    )
    private ReceiptSummaryDTO receipt;

    public ReceiptLineResponseDTO() {
    }

    public ReceiptLineResponseDTO(Long id, PersonSummaryDTO person, LocalDate date, String concept, BigDecimal amount, ReceiptLineStatus status, ReceiptSummaryDTO receipt) {
        this.id = id;
        this.person = person;
        this.date = date;
        this.concept = concept;
        this.amount = amount;
        this.status = status;
        this.receipt = receipt;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ReceiptLineStatus getStatus() {
        return status;
    }

    public void setStatus(ReceiptLineStatus status) {
        this.status = status;
    }

    public ReceiptSummaryDTO getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptSummaryDTO receipt) {
        this.receipt = receipt;
    }
}
