package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.DogSex;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class DogRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre debe tener un máximo de 50 caracteres")
    private String name;

    @NotNull
    @Past
    private LocalDate birthDate;

    private DogSex sex;

    @Size(max = 50, message = "La raza debe tener un máximo de 50 caracteres")
    private String breed;

    @NotBlank(message = "El microchip es obligatorio")
    @Pattern(regexp = "\\d{15}", message = "El microchip debe tener un máximo de 15 caracteres")
    private String microchip;

    @Size(max = 50, message = "El número de pedigree debe tener un máximo de 50 caracteres")
    private String pedigreeNumber;

    @NotNull
    private Long ownerId;

    public DogRequestDTO() {
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
