package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Información resumida de una persona."
)
public class PersonSummaryDTO {

    @Schema(
            description = "Identificador único de la persona.",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nombre de la persona.",
            example = "Daniel"
    )
    private String name;

    @Schema(
            description = "Apellidos de la persona.",
            example = "Losada Anillo"
    )
    private String lastName;

    public PersonSummaryDTO() {
    }

    public PersonSummaryDTO(Long id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
