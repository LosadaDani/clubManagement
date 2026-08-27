package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        description = "Información completa de una licencia de competición."
)
public class CompetitionLicenseResponseDTO {

    @Schema(
            description = "Identificador único de la licencia.",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "Organización emisora de la licencia."
    )
    private OrganizationSummaryDTO organization;
    @Schema(
            description = "Persona titular de la licencia."
    )
    private PersonSummaryDTO person;
    @Schema(
            description = "Perro asociado a la licencia."
    )
    private DogSummaryDTO dog;
    @Schema(
            description = "Número de licencia de competición.",
            example = "LC-2026-001"
    )
    private String licenseNumber;
    @Schema(
            description = "Fecha de inicio de vigencia de la licencia.",
            example = "2026-01-01"
    )
    private LocalDate startDate;
    @Schema(
            description = "Fecha de fin de vigencia de la licencia.",
            example = "2026-12-31"
    )
    private LocalDate endDate;

    public CompetitionLicenseResponseDTO() {
    }

    public CompetitionLicenseResponseDTO(Long id, OrganizationSummaryDTO organization, PersonSummaryDTO person, DogSummaryDTO dog, String licenseNumber, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.organization = organization;
        this.person = person;
        this.dog = dog;
        this.licenseNumber = licenseNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrganizationSummaryDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationSummaryDTO organization) {
        this.organization = organization;
    }

    public PersonSummaryDTO getPerson() {
        return person;
    }

    public void setPerson(PersonSummaryDTO person) {
        this.person = person;
    }

    public DogSummaryDTO getDog() {
        return dog;
    }

    public void setDog(DogSummaryDTO dog) {
        this.dog = dog;
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
