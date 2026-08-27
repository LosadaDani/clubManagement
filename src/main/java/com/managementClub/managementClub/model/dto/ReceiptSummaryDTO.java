package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.ReceiptStatus;

import java.time.LocalDate;

public class ReceiptSummaryDTO {
    private Long id;
    private LocalDate issueDate;
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
