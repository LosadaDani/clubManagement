package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.PersonMapper;
import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.model.dto.PersonStatusDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import com.managementClub.managementClub.service.PersonService;
import com.managementClub.managementClub.service.ReceiptLineService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final ReceiptLineService receiptLineService;
    private final CurrentUserProvider currentUserProvider;

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper, ReceiptLineService receiptLineService, CurrentUserProvider currentUserProvider) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.receiptLineService = receiptLineService;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    @Override
    public PersonResponseDTO createPerson(PersonRequestDTO dto) {

        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        if (currentUser.getRole() == Role.ROLE_TRAINER
                && dto.getMembershipType() != MembershipType.INITIATION_TRAINING
                && dto.getMembershipType() != MembershipType.PERMANENT_TRAINING) {
            throw new AccessDeniedException("Un entrenador solo puede dar de alta personas en formación.");
        }

        if(personRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe una persona con ese mail");
        }

        Person person = personMapper.toEntity(dto);

        if (person.getMemberSince() == null) {
            person.setMemberSince(LocalDate.now());
        }

        person.setMembershipStatus(MembershipStatus.ACTIVE);

        Person savedPerson = personRepository.save(person);

        if (dto.getMembershipType() == MembershipType.INITIATION_TRAINING) {
            receiptLineService.createInitiationLines(savedPerson);
        }

        return personMapper.toResponseDto(savedPerson);
    }

    @Override
    public PersonResponseDTO getPersonById(Long id) {

        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        if (currentUser.getRole() != Role.ROLE_ADMIN && !currentUser.getPerson().getId().equals(id)) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta persona");
        }

        Person person = personRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Persona con identificador " + id + " no encontrada"));

        return  personMapper.toResponseDto(person);
    }

    @Override
    public List<PersonResponseDTO> searchPersons(String searchText) {

        searchText = searchText.trim().replaceAll("\\s+", " ");

        return personRepository.searchByFullName(searchText)
                .stream()
                .map(personMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<PersonResponseDTO> getAllPersons() {

        return personRepository.findAll()
                .stream()
                .map(personMapper::toResponseDto)
                .toList();
    }

    @Override
    public PersonResponseDTO updatePerson(Long id, PersonRequestDTO personRequest) {

        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        if (currentUser.getRole() != Role.ROLE_ADMIN && !currentUser.getPerson().getId().equals(id)) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta persona");
        }

        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No existe ninguna persona con el id " + id));

        if (currentUser.getRole() != Role.ROLE_ADMIN
            && personRequest.getMembershipType() != existingPerson.getMembershipType()) {
            throw new AccessDeniedException("No tienes permiso para modificar tu tipo de membresía.");
        }

        if (!existingPerson.getEmail().equalsIgnoreCase(personRequest.getEmail())) {
            if(personRepository.findByEmail(personRequest.getEmail()).isPresent()) {
                throw new ResourceAlreadyExistsException("Ya existe una persona con ese mail");
            }
        }

        personMapper.updateEntity(existingPerson, personRequest);

        Person updatedPerson = personRepository.save(existingPerson);

        return personMapper.toResponseDto(updatedPerson);
    }

    @Override
    public PersonResponseDTO changeMembershipStatus(Long id, PersonStatusDTO dto) {

        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No existe ninguna persona con el id " + id));

        if (existingPerson.getMembershipStatus() == dto.getMembershipStatus()) {
            return personMapper.toResponseDto(existingPerson);
        }

        existingPerson.setMembershipStatus(dto.getMembershipStatus());

        return personMapper.toResponseDto(personRepository.save(existingPerson));
    }
}
