package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.DogSex;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        description = "Información completa de un perro registrado en el sistema."
)
public class DogResponseDTO {

    @Schema(
            description = "Identificador único del perro.",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nombre del perro.",
            example = "Rex"
    )
    private String name;

    @Schema(
            description = "Fecha de nacimiento del perro.",
            example = "2020-05-15"
    )
    private LocalDate birthDate;

    @Schema(
            description = "Sexo del perro.",
            example = "MALE"
    )
    private DogSex sex;

    @Schema(
            description = "Raza del perro.",
            example = "Golden Retriever"
    )
    private String breed;

    @Schema(
            description = "Número de microchip (15 dígitos).",
            example = "123456789012345"
    )
    private String microchip;

    @Schema(
            description = "Número de pedigree del perro.",
            example = "ES-2020-1234"
    )
    private String pedigreeNumber;

    @Schema(
            description = "Propietario del perro."
    )
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

