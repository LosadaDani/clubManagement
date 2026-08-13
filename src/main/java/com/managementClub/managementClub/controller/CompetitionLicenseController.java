package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.CompetitionLicenseControllerDocs;
import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.service.impl.CompetitionLicenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/competition-licenses")
public class CompetitionLicenseController implements CompetitionLicenseControllerDocs {

    private final CompetitionLicenseService competitionLicenseService;

    public CompetitionLicenseController(CompetitionLicenseService competitionLicenseService) {
        this.competitionLicenseService = competitionLicenseService;
    }

    @PostMapping
    @Override
    public ResponseEntity<CompetitionLicenseResponseDTO> createCompetitionLicense(@Valid @RequestBody CompetitionLicenseRequestDTO requestDTO) {

        CompetitionLicenseResponseDTO competitionLicense = competitionLicenseService.createCompetitionLicense(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(competitionLicense);
    }
}
