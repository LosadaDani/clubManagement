package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.PersonMapper;
import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.service.PersonService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper) {

    this.personRepository = personRepository;
    this.personMapper = personMapper;
    }

    @Override
    public PersonResponseDTO createPerson(PersonRequestDTO dto) {

        if(personRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe una persona con ese mail");
        }

        Person person = personMapper.toEntity(dto);

        if (person.getMemberSince() == null) {
            person.setMemberSince(LocalDate.now());
        }

        person.setMembershipStatus(MembershipStatus.ACTIVE);

        Person savedPerson = personRepository.save(person);

        return personMapper.toResponseDto(savedPerson);
    }

    @Override
    public PersonResponseDTO getPersonById(Long id) {

        Person person = personRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No existe ninguna persona con el id" + id));

            return  personMapper.toResponseDto(person);
    }

    @Override
    public List<PersonResponseDTO> getAllPersons() {

        return personRepository.findAll()
                .stream()
                .map(personMapper::toResponseDto)
                .toList();
    }
}
