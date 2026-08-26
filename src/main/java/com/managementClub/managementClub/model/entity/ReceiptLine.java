package com.managementClub.managementClub.model.entity;

import com.managementClub.managementClub.model.enums.ReceiptLineStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "receipt_line")
public class ReceiptLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 100)
    private String concept;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    //TODO para el service Inicialmente Pending
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReceiptLineStatus status;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    public ReceiptLine() {
    }

    public ReceiptLine(Person person, LocalDate date, String concept, BigDecimal amount, ReceiptLineStatus status, Receipt receipt) {
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
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

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
}
