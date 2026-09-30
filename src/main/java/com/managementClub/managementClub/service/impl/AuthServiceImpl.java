package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.mapper.AppUserMapper;
import com.managementClub.managementClub.model.dto.LoginRequestDTO;
import com.managementClub.managementClub.model.dto.LoginResponseDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.security.AppUserDetails;
import com.managementClub.managementClub.security.JwtService;
import com.managementClub.managementClub.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserMapper appUserMapper;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, AppUserMapper appUserMapper, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.appUserMapper = appUserMapper;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();
        AppUser appUser = appUserDetails.getAppUser();

        String token = jwtService.generateToken(appUser);

        return appUserMapper.toLoginResponseDto(appUser, token);
    }
}
