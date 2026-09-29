package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.AppUserCreatedResponseDTO;
import com.managementClub.managementClub.model.dto.AppUserRequestDTO;
import com.managementClub.managementClub.model.dto.LoginResponseDTO;
import com.managementClub.managementClub.model.dto.PersonSummaryDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUser toEntity(AppUserRequestDTO dto, Person person, String hashedPassword, Role role) {
        AppUser appUser = new AppUser();

        appUser.setUsername(dto.getUsername());
        appUser.setPerson(person);
        appUser.setPassword(hashedPassword);
        appUser.setRole(role);

        return appUser;
    }

    public AppUserCreatedResponseDTO toCreatedResponseDto(AppUser appUser, String plainPassword) {

        PersonSummaryDTO person = toPersonSummaryDto(appUser.getPerson());

        return new AppUserCreatedResponseDTO(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getRole(),
                person,
                plainPassword);
    }

    public LoginResponseDTO toLoginResponseDto(AppUser appUser) {

        PersonSummaryDTO person = toPersonSummaryDto(appUser.getPerson());

        return new LoginResponseDTO(appUser.getUsername(),
                appUser.getRole(),
                person);
    }

    private PersonSummaryDTO toPersonSummaryDto (Person person) {
        return new PersonSummaryDTO(
                person.getId(),
                person.getName(),
                person.getLastName()
        );
    }
}
