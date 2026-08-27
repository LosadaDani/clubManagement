package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Información completa de una organización."
)
public class OrganizationResponseDTO {

    @Schema(
            description = "Identificador único de la organización.",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "Nombre completo de la organización.",
            example = "Real Sociedad Canina de España"
    )
    private String name;
    @Schema(
            description = "Nombre abreviado de la organización.",
            example = "RSCE"
    )
    private String shortName;

    public OrganizationResponseDTO() {
    }

    public OrganizationResponseDTO(Long id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
