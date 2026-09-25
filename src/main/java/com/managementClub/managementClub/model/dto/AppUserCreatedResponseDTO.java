package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Información del usuario creado en el sistema."
)
public class AppUserCreatedResponseDTO {

    @Schema(
            description = "Identificador único del usuario.",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nombre de usuario.",
            example = "johndoe"
    )
    private String username;

    @Schema(
            description = "Rol del usuario.",
            example = "ROLE_USER"
    )
    private Role role;

    @Schema(
            description = "Persona asociada al usuario.",
    )
    private PersonSummaryDTO person;

    @Schema(
            description = "Contraseña del usuario.",
            example = "password123"
    )
    private String password;

    public AppUserCreatedResponseDTO() {
    }

    public AppUserCreatedResponseDTO(Long id, String username, Role role, PersonSummaryDTO person, String password) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.person = person;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PersonSummaryDTO getPerson() {
        return person;
    }

    public void setPerson(PersonSummaryDTO person) {
        this.person = person;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
