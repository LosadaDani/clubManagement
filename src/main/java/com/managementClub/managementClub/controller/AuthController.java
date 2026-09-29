package com.managementClub.managementClub.controller;

import com.managementClub.managementClub.controller.documentation.AuthControllerDocs;
import com.managementClub.managementClub.model.dto.LoginRequestDTO;
import com.managementClub.managementClub.model.dto.LoginResponseDTO;
import com.managementClub.managementClub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }
}
