package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información devuelta tras un inicio de sesión correcto.")
public class LoginResponseDTO {

    @Schema(description = "Nombre de usuario.", example = "admin")
    private String username;

    @Schema(description = "Rol del usuario.", example = "ROLE_ADMIN")
    private Role role;

    @Schema(description = "Información del usuario.")
    private PersonSummaryDTO person;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String username, Role role, PersonSummaryDTO person) {
        this.username = username;
        this.role = role;
        this.person = person;
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
}
