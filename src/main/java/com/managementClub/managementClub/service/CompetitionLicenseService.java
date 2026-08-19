package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;

import java.util.List;

public interface CompetitionLicenseService {

    CompetitionLicenseResponseDTO createCompetitionLicense(CompetitionLicenseRequestDTO competitionLicenseRequestDTO);

    List<CompetitionLicenseResponseDTO> getCompetitionLicensesByDogId(Long dogId);

    List<CompetitionLicenseResponseDTO> getLicenseCurrentByDogId(Long dogId);
}
