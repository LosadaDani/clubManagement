package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.AppUserCreatedResponseDTO;
import com.managementClub.managementClub.model.dto.AppUserRequestDTO;
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

        PersonSummaryDTO person = new PersonSummaryDTO(
                appUser.getPerson().getId(),
                appUser.getPerson().getName(),
                appUser.getPerson().getLastName()
        );

        return new AppUserCreatedResponseDTO(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getRole(),
                person,
                plainPassword);
    }
}
