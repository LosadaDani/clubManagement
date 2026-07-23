package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.DogRequestDTO;
import com.managementClub.managementClub.model.dto.DogResponseDTO;
import com.managementClub.managementClub.model.dto.PersonSummaryDTO;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Person;
import org.springframework.stereotype.Component;

@Component
public class DogMapper {

    public Dog toEntity(DogRequestDTO dogRequestDTO, Person owner) {
        Dog dog = new Dog();

        dog.setName(dogRequestDTO.getName());
        dog.setBirthDate(dogRequestDTO.getBirthDate());
        dog.setSex(dogRequestDTO.getSex());
        dog.setBreed(dogRequestDTO.getBreed());
        dog.setMicrochip(dogRequestDTO.getMicrochip());
        dog.setPedigreeNumber(dogRequestDTO.getPedigreeNumber());

        dog.setOwner(owner);
        
        return dog;
    }

    public DogResponseDTO toDTO(Dog dog) {

        PersonSummaryDTO ownerSummary = new PersonSummaryDTO(
                dog.getOwner().getId(),
                dog.getOwner().getName(),
                dog.getOwner().getLastName()
        );

        return new DogResponseDTO(dog.getId(), dog.getName(), dog.getBirthDate(), dog.getSex(),dog.getBreed(), dog.getMicrochip(),
                dog.getPedigreeNumber(), ownerSummary);
    }
}
