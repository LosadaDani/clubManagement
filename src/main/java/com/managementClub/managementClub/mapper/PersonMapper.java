package com.managementClub.managementClub.mapper;


import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.model.entity.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public Person requestDtoToToEntity (PersonRequestDTO dto) {
        Person person = new Person();

        person.setName(dto.getName());
        person.setLastName(dto.getLastName());
        person.setTelephone(dto.getTelephone());
        person.setEmail(dto.getEmail());
        person.setMemberSince(dto.getMemberSince());
        person.setMembershipType(dto.getMembershipType());

        return person;
    }

    public PersonResponseDTO entityToResponseDTO (Person person) {
        return new PersonResponseDTO(
                person.getId(),
                person.getName(),
                person.getLastName(),
                person.getTelephone(),
                person.getEmail(),
                person.getMemberSince(),
                person.getMembershipStatus(),
                person.getMembershipType()
        );
    }
}
