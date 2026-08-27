package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.DogSex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(
        description = "Datos necesarios para registrar o actualizar un perro."
)
public class DogRequestDTO {

    @Schema(
            description = "Nombre del perro.",
            example = "Rex"
    )
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre debe tener un máximo de 50 caracteres")
    private String name;

    @Schema(
            description = "Fecha de nacimiento del perro.",
            example = "2020-05-15"
    )
    @NotNull
    @Past
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
    @Size(max = 50, message = "La raza debe tener un máximo de 50 caracteres")
    private String breed;

    @Schema(
            description = "Número de microchip (15 dígitos).",
            example = "123456789012345"
    )
    @NotBlank(message = "El microchip es obligatorio")
    @Pattern(regexp = "\\d{15}", message = "El microchip debe tener exactamente 15 digitos")
    private String microchip;

    @Schema(
            description = "Número de pedigree del perro.",
            example = "ES-2020-1234"
    )
    @Size(max = 50, message = "El número de pedigree debe tener un máximo de 50 caracteres")
    private String pedigreeNumber;

    @Schema(
            description = "Identificador del propietario del perro.",
            example = "1"
    )
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
