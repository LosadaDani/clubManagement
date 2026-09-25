package com.managementClub.managementClub.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Datos necesarios para registrar n usuario."
)
public class AppUserRequestDTO {

    @Schema(
            description = "Nombre de usuario.",
            example = "johndoe"
    )
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario debe tener un máximo de 50 caracteres")
    private String username;

    @NotNull(message = "El id de la persona es obligatorio")
    private Long personId;

    public AppUserRequestDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}

