package com.managementClub.managementClub.service;

import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.model.dto.PersonStatusDTO;

import java.util.List;

public interface PersonService {

    PersonResponseDTO createPerson(PersonRequestDTO dto);

    PersonResponseDTO getPersonById(Long id);

    List<PersonResponseDTO> searchPersons(String searchText);

    List<PersonResponseDTO> getAllPersons();

    PersonResponseDTO updatePerson(Long id, PersonRequestDTO requestDto);

    PersonResponseDTO changeMembershipStatus (Long id, PersonStatusDTO dto);
}
