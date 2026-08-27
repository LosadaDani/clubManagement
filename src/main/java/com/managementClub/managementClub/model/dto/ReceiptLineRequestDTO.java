package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
        description = "Datos necesarios para registrar una línea de recibo."
)
public class ReceiptLineRequestDTO {

    @Schema(
            description = "Identificador de la persona asociada a la línea de recibo.",
            example = "1"
    )
    @NotNull(message = "La persona es obligatoria")
    private Long personId;

    @Schema(
            description = "Fecha de la línea de recibo.",
            example = "2026-08-27"
    )
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @Schema(
            description = "Concepto o descripción del pago.",
            example = "Cuota mensual agosto 2026"
    )
    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 100, message = "El concepto debe tener un máximo de 100 caracteres")
    private String concept;

    @Schema(
            description = "Importe de la línea de recibo.",
            example = "25.00"
    )
    @NotNull(message = "El monto es obligatorio")
    @Digits(integer = 10, fraction = 2, message = "El importe debe tener un máximo de 10 dígitos enteros y 2 decimales")
    private BigDecimal amount;

    public ReceiptLineRequestDTO() {
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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
}
