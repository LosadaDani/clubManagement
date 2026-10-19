package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.MembershipType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(
        description = "Datos necesarios para registrar o actualizar una persona."
)
public class PersonRequestDTO {

    @Schema(
            description = "Nombre de la persona.",
            example = "Daniel"
    )
    @NotBlank(message =  "El nombre es obligatorio.")
    @Size(max = 50, message = "El nombre debe tener un máximo de 50 caracteres.")
    private String name;

    @Schema(
            description = "Apellidos de la persona.",
            example = "Losada Anillo"
    )
    @NotBlank(message =  "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido debe tener un máximo de 100 caracteres.")
    private String lastName;

    @Schema(
            description = "Número de teléfono de contacto.",
            example = "612345678"
    )
    @Size(max = 20, message = "El teléfono debe tener un máximo de 20 caracteres.")
    private String telephone;

    @Schema(
            description = "Correo electrónico de la persona. Debe ser único dentro del sistema.",
            example = "daniel.losada@email.com"
    )
    @NotBlank(message =  "El email es obligatorio.")
    @Email( message = "El email no tiene un formato válido.")
    @Size(max = 100, message = "El email debe tener un máximo de 100 caracteres.")
    private String email;

    @Schema(
            description = "En el alta, si no se informa se asigna la fecha actual; en la modificación, si no se informa se conserva la existente",
            example = "2026-07-10"
    )
    @PastOrPresent(message = "La fecha de alta no puede ser posterior a hoy.")
    private LocalDate memberSince;

    @Schema(
            description = "Tipo de vinculación de la persona con el club.",
            example = "FULL_PARTNER"
    )
    @NotNull(message =  "El tipo de socio es obligatorio.")
    private MembershipType membershipType;

    public PersonRequestDTO() {
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }
}
