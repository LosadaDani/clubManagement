package com.managementClub.managementClub.model.dto;

import com.managementClub.managementClub.model.enums.MembershipStatus;
import jakarta.validation.constraints.NotNull;

public class PersonStatusDTO {

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
