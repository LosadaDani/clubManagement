package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.LoginRequestDTO;
import com.managementClub.managementClub.model.dto.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO dto);
}
