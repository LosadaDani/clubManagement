package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;

public interface DogService {

    DogResponseDTO createDog(DogRequestDTO dto);
}
