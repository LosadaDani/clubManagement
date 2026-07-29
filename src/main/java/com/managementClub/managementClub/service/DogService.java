package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;

import java.util.List;

public interface DogService {

    DogResponseDTO createDog(DogRequestDTO dto);

    DogResponseDTO getDogById(Long id);

    List<DogResponseDTO> getDogsByPersonId(Long personId);

    List<DogResponseDTO> getAllDogs();
}
