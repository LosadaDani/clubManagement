package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.CompetitionLicenseMapper;
import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.model.entity.CompetitionLicense;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.repository.CompetitionLicenseRepository;
import com.managementClub.managementClub.repository.DogRepository;
import com.managementClub.managementClub.repository.OrganizationRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.service.CompetitionLicenseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CompetitionLicenseServiceImpl implements CompetitionLicenseService {
    private final CompetitionLicenseRepository competitionLicenseRepository;
    private final OrganizationRepository organizationRepository;
    private final PersonRepository personRepository;
    private final DogRepository dogRepository;
    private final CompetitionLicenseMapper competitionLicenseMapper;

    public CompetitionLicenseServiceImpl(CompetitionLicenseRepository competitionLicenseRepository, OrganizationRepository organizationRepository, PersonRepository personRepository, DogRepository dogRepository, CompetitionLicenseMapper competitionLicenseMapper) {
        this.competitionLicenseRepository = competitionLicenseRepository;
        this.organizationRepository = organizationRepository;
        this.personRepository = personRepository;
        this.dogRepository = dogRepository;
        this.competitionLicenseMapper = competitionLicenseMapper;
    }

    @Override
    public CompetitionLicenseResponseDTO createCompetitionLicense(CompetitionLicenseRequestDTO competitionLicenseRequestDTO) {

        Organization organization = organizationRepository.findById(competitionLicenseRequestDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("La organización indicada no existe"));

        Person person = personRepository.findById(competitionLicenseRequestDTO.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("La persona indicada no existe"));

        Dog dog = dogRepository.findById(competitionLicenseRequestDTO.getDogId())
                .orElseThrow(() -> new ResourceNotFoundException("El perro indicado no existe"));

        if (!Objects.equals(dog.getOwner().getId(), competitionLicenseRequestDTO.getPersonId())) {
            throw new InvalidBusinessRuleException("El propietario del perro no coincide con el indicado en la licencia");
        }

        if (competitionLicenseRequestDTO.getStartDate().isAfter(competitionLicenseRequestDTO.getEndDate())) {
            throw new InvalidBusinessRuleException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (competitionLicenseRepository.findByOrganizationAndPersonAndDogAndStartDateBeforeAndEndDateAfter(
                organization, person, dog, competitionLicenseRequestDTO.getEndDate(), competitionLicenseRequestDTO.getStartDate())
                .isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe una licencia para la misma organización, persona y perro con periodo de vigencia solapado");
        }

        CompetitionLicense competitionLicense = competitionLicenseMapper.toEntity(competitionLicenseRequestDTO, organization, person, dog);
        CompetitionLicense savedCompetitionLicense = competitionLicenseRepository.save(competitionLicense);
        return competitionLicenseMapper.toResponseDTO(savedCompetitionLicense);
    }

    @Override
    public List<CompetitionLicenseResponseDTO> getCompetitionLicensesByDogId(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new ResourceNotFoundException("El perro indicado no existe"));

        return competitionLicenseRepository.findByDog(dog)
                .stream()
                .map(competitionLicense -> competitionLicenseMapper.toResponseDTO(competitionLicense))
                .toList();
    }

    @Override
    public List<CompetitionLicenseResponseDTO> getLicenseCurrentByDogId(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new ResourceNotFoundException("El perro indicado no existe"));

        return competitionLicenseRepository.findLicenseCurrentByDog(dog)
                .stream()
                .map(competitionLicenseMapper::toResponseDTO)
                .toList();
    }

    @Override
    public CompetitionLicenseResponseDTO updateCompetitionLicense(Long id, CompetitionLicenseRequestDTO competitionLicenseRequestDTO) {

        CompetitionLicense existingCompetitionLicense = competitionLicenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe ninguna licencia con el id " + id));

        Organization organization = organizationRepository.findById(competitionLicenseRequestDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("La organización indicada no existe"));

        Person person = personRepository.findById(competitionLicenseRequestDTO.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("La persona indicada no existe"));

        Dog dog = dogRepository.findById(competitionLicenseRequestDTO.getDogId())
                .orElseThrow(() -> new ResourceNotFoundException("El perro indicado no existe"));

        if (!Objects.equals(dog.getOwner().getId(), competitionLicenseRequestDTO.getPersonId())) {
            throw new InvalidBusinessRuleException("El propietario del perro no coincide con el indicado en la licencia");
        }

        if (competitionLicenseRequestDTO.getStartDate().isAfter(competitionLicenseRequestDTO.getEndDate())) {
            throw new InvalidBusinessRuleException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (competitionLicenseRepository.findOverlappingLicenseExcludingId(organization, person, dog, competitionLicenseRequestDTO.getEndDate(), competitionLicenseRequestDTO.getStartDate(), id)
                .isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe una licencia para la misma organización, persona y perro con periodo de vigencia solapado");
        }

        competitionLicenseMapper.updateEntity(existingCompetitionLicense, competitionLicenseRequestDTO, organization, person, dog);
        CompetitionLicense savedCompetitionLicense = competitionLicenseRepository.save(existingCompetitionLicense);
        return competitionLicenseMapper.toResponseDTO(savedCompetitionLicense);
    }
}
