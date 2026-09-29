package com.managementClub.managementClub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos necesarios para iniciar sesión.")
public class LoginRequestDTO {

    @Schema(description = "Nombre de usuario.", example = "admin")
    @NotBlank(message = "El nombre de usuario no puede ser nulo.")
    private String username;

    @Schema(description = "Contraseña del usuario.", example = "admin123")
    @NotBlank(message = "La contraseña no puede ser nula.")
    private String password;

    public LoginRequestDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
