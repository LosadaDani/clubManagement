package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Datos necesarios para registrar o actualizar una organización."
)
public class OrganizationRequestDTO {

    @Schema(
            description = "Nombre completo de la organización.",
            example = "Real Sociedad Canina de España"
    )
    @NotBlank(message = "El nombre de la organización es obligatorio.")
    @Size(max = 100, message = "El nombre de la organización debe tener un máximo de 100 caracteres.")
    private String name;

    @Schema(
            description = "Nombre abreviado de la organización.",
            example = "RSCE"
    )
    @NotBlank(message = "El nombre abreviado de la organización es obligatorio.")
    @Size(max = 20, message = "El nombre abreviado de la organización debe tener un máximo de 20 caracteres.")
    private String shortName;

    public OrganizationRequestDTO() {
    }

    public OrganizationRequestDTO(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
