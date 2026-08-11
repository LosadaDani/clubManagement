package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.OrganizationRequestDTO;
import com.managementClub.managementClub.model.dto.OrganizationResponseDTO;
import com.managementClub.managementClub.model.entity.Organization;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    public Organization toEntity(OrganizationRequestDTO organizationRequestDTO) {
        Organization organization = new Organization();
        organization.setName(organizationRequestDTO.getName());
        organization.setShortName(organizationRequestDTO.getShortName());
        return organization;
    }

    public OrganizationResponseDTO toResponseDto(Organization organization) {
        return new OrganizationResponseDTO(organization.getId(),
                organization.getName(),
                organization.getShortName());
    }

    public void updateEntity(Organization organization, OrganizationRequestDTO organizationRequestDTO) {
        organization.setName(organizationRequestDTO.getName());
        organization.setShortName(organizationRequestDTO.getShortName());
    }
}
