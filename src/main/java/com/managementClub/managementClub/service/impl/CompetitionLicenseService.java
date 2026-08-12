package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;

public interface CompetitionLicenseService {

    CompetitionLicenseResponseDTO createCompetitionLicense(CompetitionLicenseRequestDTO competitionLicenseRequestDTO);
}
