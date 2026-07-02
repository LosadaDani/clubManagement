package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;

public interface PersonService {

    PersonResponseDTO createPerson(PersonRequestDTO dto);
}
