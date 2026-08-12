package com.managementClub.managementClub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CompetitionLicenseRequestDTO {

    @NotNull(message = "La organización de la licencia es obligatoria")
    private Long organizationId;

    @NotNull(message = "La persona de la licencia es obligatoria")
    private Long personId;

    @NotNull(message = "El perro de la licencia es obligatorio")
    private Long dogId;

    @NotBlank(message = "El número de licencia es obligatorio")
    @Size(max = 50, message = "El número de licencia debe tener un máximo de 50 caracteres")
    private String licenseNumber;

    @NotNull(message = "La fecha de inicio de la licencia es obligatoria")
    private LocalDate startDate;

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
