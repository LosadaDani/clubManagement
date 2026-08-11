package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.OrganizationControllerDocs;
import com.managementClub.managementClub.model.dto.OrganizationRequestDTO;
import com.managementClub.managementClub.model.dto.OrganizationResponseDTO;
import com.managementClub.managementClub.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController implements OrganizationControllerDocs {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    @Override
    public ResponseEntity<OrganizationResponseDTO> createOrganization(@Valid @RequestBody OrganizationRequestDTO requestDto) {
        OrganizationResponseDTO organization = organizationService.createOrganization(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(organization);
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<OrganizationResponseDTO> updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationRequestDTO requestDTO) {
        OrganizationResponseDTO responseDto = organizationService.updateOrganization(id, requestDTO);

        return ResponseEntity.ok(responseDto);
    }
}
