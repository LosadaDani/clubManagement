package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseUpdateRequestDTO;

import java.util.List;

public interface CompetitionLicenseService {

    CompetitionLicenseResponseDTO createCompetitionLicense(CompetitionLicenseRequestDTO competitionLicenseRequestDTO);

    List<CompetitionLicenseResponseDTO> getCompetitionLicensesByDogId(Long dogId);

    List<CompetitionLicenseResponseDTO> getLicenseCurrentByDogId(Long dogId);

    CompetitionLicenseResponseDTO updateCompetitionLicense(Long id, CompetitionLicenseUpdateRequestDTO competitionLicenseUpdateRequestDTO);
}
