package com.managementClub.managementClub.model.dto;

import java.time.LocalDate;

public class CompetitionLicenseResponseDTO {

    private Long id;
    private Long organizationId;
    private Long personId;
    private Long dogId;
    private String licenseNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    public CompetitionLicenseResponseDTO() {
    }

    public CompetitionLicenseResponseDTO(Long id, Long organizationId, Long personId, Long dogId, String licenseNumber, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.organizationId = organizationId;
        this.personId = personId;
        this.dogId = dogId;
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
