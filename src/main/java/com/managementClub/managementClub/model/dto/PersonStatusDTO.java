package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.MembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Datos necesarios para modificar el estado de una persona."
)
public class PersonStatusDTO {

    @Schema(
            description = "Nuevo estado que se asignará a la persona.",
            example = "INACTIVE"
    )
    @NotNull(message = "El estado es obligatorio")
    private MembershipStatus membershipStatus;

    public PersonStatusDTO() {
    }

    public PersonStatusDTO(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }
}
