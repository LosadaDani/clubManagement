package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.DogSex;

import java.time.LocalDate;

public class DogResponseDTO {

    private Long id;

    private String name;

    private LocalDate birthDate;

    private DogSex sex;

    private String breed;

    private String microchip;

    private String pedigreeNumber;

    private PersonSummaryDTO owner;

    public DogResponseDTO() {
    }

    public DogResponseDTO(Long id, String name, LocalDate birthDate, DogSex sex,String breed, String microchip, String pedigreeNumber, PersonSummaryDTO owner) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.sex = sex;
        this.breed = breed;
        this.microchip = microchip;
        this.pedigreeNumber = pedigreeNumber;
        this.owner = owner;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public DogSex getSex() {
        return sex;
    }

    public void setSex(DogSex sex) {
        this.sex = sex;
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

    public PersonSummaryDTO getOwner() {
        return owner;
    }

    public void setOwner(PersonSummaryDTO owner) {
        this.owner = owner;
    }
}

