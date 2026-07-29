package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.DogMapper;
import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.repository.DogRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.service.DogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final PersonRepository personRepository;
    private final DogMapper dogMapper;

    public DogServiceImpl(DogRepository dogRepository, DogMapper dogMapper, PersonRepository personRepository) {
        this.dogRepository = dogRepository;
        this.personRepository = personRepository;
        this.dogMapper = dogMapper;
    }

    @Override
    public DogResponseDTO createDog(DogRequestDTO dto) {

        Person owner = personRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Propietario no encontrado"));

        if (dogRepository.findByMicrochip(dto.getMicrochip()).isPresent()) {
            throw new ResourceAlreadyExistsException("Microchip existente");
        }

        if (dto.getPedigreeNumber() != null && dogRepository.findByPedigreeNumber(dto.getPedigreeNumber()).isPresent()) {
            throw new ResourceAlreadyExistsException("Numero de pedigree existente");
        }

        Dog dog = dogMapper.toEntity(dto, owner);

        Dog savedDog = dogRepository.save(dog);

        return dogMapper.toResponseDto(savedDog);
    }

    @Override
    public List<DogResponseDTO> getAllDogs() {
        return dogRepository.findAll()
                .stream()
                .map(dog -> dogMapper.toResponseDto(dog))
                .toList();
    }


}
