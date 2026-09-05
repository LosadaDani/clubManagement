package com.managementClub.managementClub.model.entity;

import com.managementClub.managementClub.model.enums.ReceiptStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "receipt")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    public Receipt() {
    }

    public Receipt(Person person, LocalDate issueDate, BigDecimal total, ReceiptStatus status) {
        this.person = person;
        this.issueDate = issueDate;
        this.total = total;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public ReceiptStatus getStatus() {
        return status;
    }

    public void setStatus(ReceiptStatus status) {
        this.status = status;
    }
}
