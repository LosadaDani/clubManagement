package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.AppUserControllerDocs;
import com.managementClub.managementClub.model.dto.AppUserCreatedResponseDTO;
import com.managementClub.managementClub.model.dto.AppUserRequestDTO;
import com.managementClub.managementClub.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-users")
public class AppUserController implements AppUserControllerDocs {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping
    @Override
    public ResponseEntity<AppUserCreatedResponseDTO> generateUser(@Valid @RequestBody AppUserRequestDTO requestDTO) {
        AppUserCreatedResponseDTO response = appUserService.registerUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
