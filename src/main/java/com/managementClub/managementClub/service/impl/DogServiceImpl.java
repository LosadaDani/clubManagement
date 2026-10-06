package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.DogMapper;
import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.DogRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import com.managementClub.managementClub.service.DogService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final PersonRepository personRepository;
    private final DogMapper dogMapper;
    private final CurrentUserProvider currentUserProvider;

    public DogServiceImpl(DogRepository dogRepository, DogMapper dogMapper, PersonRepository personRepository, CurrentUserProvider currentUserProvider) {
        this.dogRepository = dogRepository;
        this.personRepository = personRepository;
        this.dogMapper = dogMapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public DogResponseDTO createDog(DogRequestDTO dto) {

        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        if (currentUser.getRole() != Role.ROLE_ADMIN
                && !currentUser.getPerson().getId().equals(dto.getOwnerId())) {
            throw new AccessDeniedException("No tienes permiso para crear perros para esta persona.");
        }

        Person owner = personRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Propietario no encontrado"));

        if (dogRepository.findByMicrochip(dto.getMicrochip()).isPresent()) {
            throw new ResourceAlreadyExistsException("Microchip existente");
        }

        if (dto.getPedigreeNumber() != null && !dto.getPedigreeNumber().isBlank() && dogRepository.findByPedigreeNumber(dto.getPedigreeNumber()).isPresent()) {
            throw new ResourceAlreadyExistsException("Numero de pedigree existente");
        }

        Dog dog = dogMapper.toEntity(dto, owner);

        Dog savedDog = dogRepository.save(dog);

        return dogMapper.toResponseDto(savedDog);
    }

    @Override
    public DogResponseDTO getDogById(Long id) {

        Optional<Dog> dogOptional = dogRepository.findById(id);
        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        boolean isOwner = dogOptional
                .map(dog -> dog.getOwner().getId().equals(currentUser.getPerson().getId()))
                .orElse(false);

        if (currentUser.getRole() != Role.ROLE_ADMIN && !isOwner) {
            throw new AccessDeniedException("No tienes permiso para ver este perro.");
        }

        Dog dog = dogOptional.orElseThrow(() ->
                        new ResourceNotFoundException("Perro con identificador " + id + " no encontrado."));

        return dogMapper.toResponseDto(dog);
    }

    @Override
    public List<DogResponseDTO> getDogsByPersonId(Long personId) {

        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        if (currentUser.getRole() != Role.ROLE_ADMIN
                && !currentUser.getPerson().getId().equals(personId)) {
            throw new AccessDeniedException("No tienes permiso para ver los perros de esta persona.");
        }
        personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona con identificador " + personId + " no encontrada."));

        List<Dog> dogs = dogRepository.findByOwnerId(personId);

        return dogs.stream()
                .map(dog -> dogMapper.toResponseDto(dog))
                .toList();
    }

    @Override
    public DogResponseDTO getDogByMicrochip(String microchip) {
        Dog dog = dogRepository.findByMicrochip(microchip)
                .orElseThrow(() -> new ResourceNotFoundException("Perro con microchip " + microchip + " no encontrado"));
        return dogMapper.toResponseDto(dog);
    }

    @Override
    public List<DogResponseDTO> getDogByName(String name) {
        return dogRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(dog -> dogMapper.toResponseDto(dog))
                .toList();
    }


    @Override
    public List<DogResponseDTO> getAllDogs() {
        return dogRepository.findAll()
                .stream()
                .map(dog -> dogMapper.toResponseDto(dog))
                .toList();
    }

    @Override
    public DogResponseDTO updateDog(Long id, DogRequestDTO dogRequest) {

        Optional<Dog> dogOptional = dogRepository.findById(id);
        AppUser currentUser = currentUserProvider.getCurrentAppUser();

        boolean isOwner = dogOptional
                .map(dog -> dog.getOwner().getId().equals(currentUser.getPerson().getId()))
                .orElse(false);

        if (currentUser.getRole() != Role.ROLE_ADMIN && !isOwner) {
            throw new AccessDeniedException("No tienes permiso para modificar este perro.");
        }

        Dog existingDog = dogOptional.orElseThrow(() ->
            new ResourceNotFoundException("Perro con identificador " + id + " no encontrado."));

        if (!Objects.equals(dogRequest.getMicrochip(), existingDog.getMicrochip())) {
                dogRepository.findByMicrochip(dogRequest.getMicrochip())
                        .ifPresent(d -> {
                            throw new ResourceAlreadyExistsException("Ya existe un perro con el microchip indicado.");
                        });
        }

        if (!Objects.equals(dogRequest.getPedigreeNumber(), existingDog.getPedigreeNumber())) {
            if (dogRequest.getPedigreeNumber() != null && !dogRequest.getPedigreeNumber().isBlank()) {
                dogRepository.findByPedigreeNumber(dogRequest.getPedigreeNumber())
                        .ifPresent(d -> {
                            throw new ResourceAlreadyExistsException("Ya existe un perro con el número de pedigree indicado.");
                        });
            }
        }

        dogMapper.updateEntity(existingDog, dogRequest);

        Dog updatedDog = dogRepository.save(existingDog);

        return dogMapper.toResponseDto(updatedDog);
    }
}
