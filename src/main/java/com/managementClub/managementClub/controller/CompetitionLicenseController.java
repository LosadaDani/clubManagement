package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.CompetitionLicenseControllerDocs;
import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.service.CompetitionLicenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/dog/{dogId}")
    @Override
    public ResponseEntity<List<CompetitionLicenseResponseDTO>> getCompetitionLicensesByDogId(@PathVariable Long dogId) {
        List<CompetitionLicenseResponseDTO>  response = competitionLicenseService.getCompetitionLicensesByDogId(dogId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dog/{dogId}/current")
    @Override
    public ResponseEntity<List<CompetitionLicenseResponseDTO>> getLicenseCurrentByDogId(@PathVariable Long dogId) {
        List<CompetitionLicenseResponseDTO> response = competitionLicenseService.getLicenseCurrentByDogId(dogId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<CompetitionLicenseResponseDTO> updateCompetitionLicense(@PathVariable Long id, @Valid @RequestBody CompetitionLicenseRequestDTO requestDTO) {
        CompetitionLicenseResponseDTO response = competitionLicenseService.updateCompetitionLicense(id, requestDTO);

        return ResponseEntity.ok(response);
    }
}
