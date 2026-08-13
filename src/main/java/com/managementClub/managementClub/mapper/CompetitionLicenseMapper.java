package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.*;
import com.managementClub.managementClub.model.entity.CompetitionLicense;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.model.entity.Person;
import org.springframework.stereotype.Component;

@Component
public class CompetitionLicenseMapper {

    public CompetitionLicense toEntity (CompetitionLicenseRequestDTO dto, Organization organization, Person person, Dog dog) {
        CompetitionLicense competitionLicense = new CompetitionLicense();

        competitionLicense.setOrganization(organization);
        competitionLicense.setPerson(person);
        competitionLicense.setDog(dog);
        competitionLicense.setLicenseNumber(dto.getLicenseNumber());
        competitionLicense.setStartDate(dto.getStartDate());
        competitionLicense.setEndDate(dto.getEndDate());

        return competitionLicense;
    }

    public CompetitionLicenseResponseDTO toResponseDTO (CompetitionLicense competitionLicense) {

        OrganizationSummaryDTO organization = new OrganizationSummaryDTO(competitionLicense.getOrganization().getId(),
                competitionLicense.getOrganization().getName(),
                competitionLicense.getOrganization().getShortName());

        PersonSummaryDTO person = new PersonSummaryDTO(competitionLicense.getPerson().getId(),
                competitionLicense.getPerson().getName(),
                competitionLicense.getPerson().getLastName());

        DogSummaryDTO dog = new DogSummaryDTO(competitionLicense.getDog().getId(),
                competitionLicense.getDog().getName(),
                competitionLicense.getDog().getBreed(),
                competitionLicense.getDog().getMicrochip(),
                competitionLicense.getDog().getPedigreeNumber());


        return new CompetitionLicenseResponseDTO(competitionLicense.getId(),
                organization,
                person,
                dog,
                competitionLicense.getLicenseNumber(),
                competitionLicense.getStartDate(),
                competitionLicense.getEndDate());
    }
}
