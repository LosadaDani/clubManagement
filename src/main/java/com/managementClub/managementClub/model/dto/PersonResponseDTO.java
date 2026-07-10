package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        description = "Información completa de una persona registrada en el sistema."
)
public class PersonResponseDTO {

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

    @Schema(
            description = "Número de teléfono de contacto.",
            example = "612345678"
    )
    private String telephone;

    @Schema(
            description = "Correo electrónico de la persona. Debe ser único dentro del sistema.",
            example = "daniel.losada@email.com"
    )
    private String email;

    @Schema(
            description = "Fecha de alta de la persona. Si no se informa, se asignará la fecha actual.",
            example = "2026-07-10"
    )
    private LocalDate memberSince;

    @Schema(
            description = "Estado actual de la persona dentro del club.",
            example = "ACTIVE"
    )
    private MembershipStatus membershipStatus;

    @Schema(
            description = "Tipo de vinculación de la persona con el club.",
            example = "FULL_PARTNER"
    )
    private MembershipType membershipType;

    public PersonResponseDTO() {
    }

    public PersonResponseDTO(Long id, String name, String lastName, String telephone, String email, LocalDate memberSince, MembershipStatus membershipStatus, MembershipType membershipType) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.telephone = telephone;
        this.email = email;
        this.memberSince = memberSince;
        this.membershipStatus = membershipStatus;
        this.membershipType = membershipType;
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

    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }
}
