package com.managementClub.managementClub.model.dto;

import java.time.LocalDate;

public class CompetitionLicenseResponseDTO {

    private Long id;
    private OrganizationSummaryDTO organization;
    private PersonSummaryDTO person;
    private DogSummaryDTO dog;
    private String licenseNumber;
    private LocalDate startDate;
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
