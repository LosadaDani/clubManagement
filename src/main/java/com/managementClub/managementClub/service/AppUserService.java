package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.AppUserCreatedResponseDTO;
import com.managementClub.managementClub.model.dto.AppUserRequestDTO;

public interface AppUserService {

    AppUserCreatedResponseDTO registerUser(AppUserRequestDTO appUserDto);

}
