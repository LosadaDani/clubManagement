package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.mapper.AppUserMapper;
import com.managementClub.managementClub.model.dto.LoginRequestDTO;
import com.managementClub.managementClub.model.dto.LoginResponseDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.security.AppUserDetails;
import com.managementClub.managementClub.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserMapper appUserMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager, AppUserMapper appUserMapper) {
        this.authenticationManager = authenticationManager;
        this.appUserMapper = appUserMapper;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();
        AppUser appUser = appUserDetails.getAppUser();

        return appUserMapper.toLoginResponseDto(appUser);
    }
}
