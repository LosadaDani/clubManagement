package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        description = "Datos necesarios para registrar o actualizar una licencia de competición."
)
public class CompetitionLicenseRequestDTO {

    @Schema(
            description = "Identificador de la organización de la licencia.",
            example = "1"
    )
    @NotNull(message = "La organización de la licencia es obligatoria")
    private Long organizationId;

    @Schema(
            description = "Identificador de la persona titular de la licencia.",
            example = "1"
    )
    @NotNull(message = "La persona de la licencia es obligatoria")
    private Long personId;

    @Schema(
            description = "Identificador del perro asociado a la licencia.",
            example = "1"
    )
    @NotNull(message = "El perro de la licencia es obligatorio")
    private Long dogId;

    @Schema(
            description = "Número de licencia de competición.",
            example = "LC-2026-001"
    )
    @NotBlank(message = "El número de licencia es obligatorio")
    @Size(max = 50, message = "El número de licencia debe tener un máximo de 50 caracteres")
    private String licenseNumber;

    @Schema(
            description = "Fecha de inicio de vigencia de la licencia.",
            example = "2026-01-01"
    )
    @NotNull(message = "La fecha de inicio de la licencia es obligatoria")
    private LocalDate startDate;

    @Schema(
            description = "Fecha de fin de vigencia de la licencia.",
            example = "2026-12-31"
    )
    @NotNull(message = "La fecha de fin de la licencia es obligatoria")
    private LocalDate endDate;

    public CompetitionLicenseRequestDTO() {
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getDogId() {
        return dogId;
    }

    public void setDogId(Long dogId) {
        this.dogId = dogId;
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
