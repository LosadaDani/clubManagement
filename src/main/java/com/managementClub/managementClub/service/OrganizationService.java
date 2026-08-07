package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.OrganizationRequestDTO;
import com.managementClub.managementClub.model.dto.OrganizationResponseDTO;

public interface OrganizationService {

    OrganizationResponseDTO createOrganization(OrganizationRequestDTO organizationRequestDTO);
}
