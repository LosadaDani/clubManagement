package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.CompetitionLicenseMapper;
import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseUpdateRequestDTO;
import com.managementClub.managementClub.model.entity.*;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.CompetitionLicenseRepository;
import com.managementClub.managementClub.repository.DogRepository;
import com.managementClub.managementClub.repository.OrganizationRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import com.managementClub.managementClub.service.CompetitionLicenseService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CompetitionLicenseServiceImpl implements CompetitionLicenseService {
    private final CompetitionLicenseRepository competitionLicenseRepository;
    private final OrganizationRepository organizationRepository;
    private final PersonRepository personRepository;
    private final DogRepository dogRepository;
    private final CompetitionLicenseMapper competitionLicenseMapper;
    private final CurrentUserProvider currentUserProvider;

    public CompetitionLicenseServiceImpl(CompetitionLicenseRepository competitionLicenseRepository, OrganizationRepository organizationRepository, PersonRepository personRepository, DogRepository dogRepository, CompetitionLicenseMapper competitionLicenseMapper, CurrentUserProvider currentUserProvider) {
        this.competitionLicenseRepository = competitionLicenseRepository;
        this.organizationRepository = organizationRepository;
        this.personRepository = personRepository;
        this.dogRepository = dogRepository;
        this.competitionLicenseMapper = competitionLicenseMapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public CompetitionLicenseResponseDTO createCompetitionLicense(CompetitionLicenseRequestDTO competitionLicenseRequestDTO) {

        AppUser currentUser = currentUserProvider.getCurrentAppUser();
        Optional<Dog> optionalDog = dogRepository.findById(competitionLicenseRequestDTO.getDogId());

        boolean isOwner = optionalDog
                .map(dog -> dog.getOwner().getId().equals(currentUser.getPerson().getId()))
                .orElse(false);

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN)
                && (!competitionLicenseRequestDTO.getPersonId().equals(currentUser.getPerson().getId()) || !isOwner)) {
            throw new AccessDeniedException("No tienes permiso para crear esta licencia.");
        }

        Organization organization = organizationRepository.findById(competitionLicenseRequestDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organización con identificador " + competitionLicenseRequestDTO.getOrganizationId() + " no encontrada."));

        Person person = personRepository.findById(competitionLicenseRequestDTO.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Persona con identificador " + competitionLicenseRequestDTO.getPersonId() + " no encontrada."));

        Dog dog = optionalDog.orElseThrow (() ->
                new ResourceNotFoundException("Perro con identificador " + competitionLicenseRequestDTO.getDogId() + " no encontrado."));

        if (!Objects.equals(dog.getOwner().getId(), competitionLicenseRequestDTO.getPersonId())) {
            throw new InvalidBusinessRuleException("El propietario del perro no coincide con el indicado en la licencia.");
        }

        if (competitionLicenseRequestDTO.getStartDate().isAfter(competitionLicenseRequestDTO.getEndDate())) {
            throw new InvalidBusinessRuleException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        if (competitionLicenseRepository.existsByOrganizationAndPersonAndDogAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                organization, person, dog, competitionLicenseRequestDTO.getEndDate(), competitionLicenseRequestDTO.getStartDate())) {
            throw new ResourceAlreadyExistsException("Ya existe una licencia para la misma organización, persona y perro con periodo de vigencia solapado.");
        }

        CompetitionLicense competitionLicense = competitionLicenseMapper.toEntity(competitionLicenseRequestDTO, organization, person, dog);
        CompetitionLicense savedCompetitionLicense = competitionLicenseRepository.save(competitionLicense);
        return competitionLicenseMapper.toResponseDTO(savedCompetitionLicense);
    }

    @Override
    public List<CompetitionLicenseResponseDTO> getCompetitionLicensesByDogId(Long dogId) {
        Dog dog = getAuthorizedDog(dogId);

        return competitionLicenseRepository.findByDog(dog)
                .stream()
                .map(competitionLicense -> competitionLicenseMapper.toResponseDTO(competitionLicense))
                .toList();
    }

    @Override
    public List<CompetitionLicenseResponseDTO> getLicenseCurrentByDogId(Long dogId) {
        Dog dog = getAuthorizedDog(dogId);

        return competitionLicenseRepository.findLicenseCurrentByDog(dog)
                .stream()
                .map(competitionLicenseMapper::toResponseDTO)
                .toList();
    }

    @Override
    public CompetitionLicenseResponseDTO updateCompetitionLicense(Long id, CompetitionLicenseUpdateRequestDTO competitionLicenseUpdateRequestDTO) {

        CompetitionLicense existingCompetitionLicense = competitionLicenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Licencia con identificador " + id + " no encontrada."));

        if (competitionLicenseUpdateRequestDTO.getStartDate().isAfter(competitionLicenseUpdateRequestDTO.getEndDate())) {
            throw new InvalidBusinessRuleException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        if (competitionLicenseRepository.existsOverlappingLicenseExcludingId(existingCompetitionLicense.getOrganization(), existingCompetitionLicense.getPerson(), existingCompetitionLicense.getDog(),
                competitionLicenseUpdateRequestDTO.getEndDate(), competitionLicenseUpdateRequestDTO.getStartDate(), id)) {
            throw new ResourceAlreadyExistsException("Ya existe una licencia para la misma organización, persona y perro con periodo de vigencia solapado.");
        }

        competitionLicenseMapper.updateEntity(existingCompetitionLicense, competitionLicenseUpdateRequestDTO);
        CompetitionLicense savedCompetitionLicense = competitionLicenseRepository.save(existingCompetitionLicense);
        return competitionLicenseMapper.toResponseDTO(savedCompetitionLicense);
    }

    private Dog getAuthorizedDog(Long dogId) {

        Optional<Dog> optionalDog = dogRepository.findById(dogId);
        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        boolean isOwner = optionalDog
                .map(dog -> dog.getOwner().getId().equals(currentUser.getPerson().getId()))
                .orElse(false);

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !isOwner ) {
            throw new AccessDeniedException("No tienes permiso para acceder a las licencias de este perro.");
        }

        Dog dog = optionalDog.orElseThrow(() ->
                new ResourceNotFoundException("Perro con identificador " + dogId + " no encontrado."));

        return dog;
    }
}
