package com.managementClub.managementClub.model.dto;

public class DogSummaryDTO {

    private Long id;
    private String name;
    private String breed;
    private String microchip;
    private String pedigreeNumber;

    public DogSummaryDTO() {
    }

    public DogSummaryDTO(Long id, String name, String breed, String microchip, String pedigreeNumber) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.microchip = microchip;
        this.pedigreeNumber = pedigreeNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public String getPedigreeNumber() {
        return pedigreeNumber;
    }

    public void setPedigreeNumber(String pedigreeNumber) {
        this.pedigreeNumber = pedigreeNumber;
    }

}
