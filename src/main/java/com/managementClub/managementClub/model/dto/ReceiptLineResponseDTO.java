package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.ReceiptLineStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceiptLineResponseDTO {

    private Long id;
    private PersonSummaryDTO person;
    private LocalDate date;
    private String concept;
    private BigDecimal amount;
    private ReceiptLineStatus status;
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
