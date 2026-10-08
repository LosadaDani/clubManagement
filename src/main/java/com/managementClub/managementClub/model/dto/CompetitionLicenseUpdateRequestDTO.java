package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        description = "Datos para actualizar una licencia de competición"
)
public class CompetitionLicenseUpdateRequestDTO {

    @Schema(
            description = "El número de licencia de competición",
            example = "LC-2026-001"
    )
    @NotBlank(message = "El número de licencia es obligatorio")
    @Size(max = 50, message = "El número de licencia debe tener un máximo de 50 caracteres")
    private String licenseNumber;

    @Schema(
            description = "La fecha de inicio de vigencia de la licencia de competición",
            example = "2026-01-01"
    )
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @Schema(
            description = "La fecha de fin de vigencia de la licencia de competición",
            example = "2026-12-31"
    )
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    public CompetitionLicenseUpdateRequestDTO() {
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
