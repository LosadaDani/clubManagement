package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.ReceiptStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Representa el detalle de un recibo con sus lineas")
public class ReceiptDetailResponseDTO {

    @Schema(description = "ID del recibo",
            example = "1")
    private Long id;

    @Schema(description = "Persona al que se le paga el recibo")
    private PersonSummaryDTO person;

    @Schema(description = "Fecha de emisión del recibo",
            example = "2023-01-01")
    private LocalDate issueDate;

    @Schema(description = "Monto del recibo",
            example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Estado del recibo",
            example = "PENDING")
    private ReceiptStatus status;

    @Schema(description = "Lineas del recibo")
    private List<ReceiptLineResponseDTO> lines;

    public ReceiptDetailResponseDTO() {
    }

    public ReceiptDetailResponseDTO(Long id, PersonSummaryDTO person, LocalDate issueDate, BigDecimal amount, ReceiptStatus status, List<ReceiptLineResponseDTO> lines) {
        this.id = id;
        this.person = person;
        this.issueDate = issueDate;
        this.amount = amount;
        this.status = status;
        this.lines = lines;
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

    public List<ReceiptLineResponseDTO> getLines() {
        return lines;
    }

    public void setLines(List<ReceiptLineResponseDTO> lines) {
        this.lines = lines;
    }
}
