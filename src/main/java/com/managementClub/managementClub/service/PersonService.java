package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;

import java.util.List;

public interface PersonService {

    PersonResponseDTO createPerson(PersonRequestDTO dto);

    PersonResponseDTO getPersonById(Long id);

    List<PersonResponseDTO> getAllPersons();
}
