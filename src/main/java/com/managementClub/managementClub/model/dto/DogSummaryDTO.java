package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Información resumida de un perro."
)
public class DogSummaryDTO {

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
