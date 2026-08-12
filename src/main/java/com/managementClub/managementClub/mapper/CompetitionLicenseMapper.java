package com.managementClub.managementClub.mapper;

import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
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

        return new CompetitionLicenseResponseDTO(competitionLicense.getId(),
                competitionLicense.getOrganization().getId(),
                competitionLicense.getPerson().getId(),
                competitionLicense.getDog().getId(),
                competitionLicense.getLicenseNumber(),
                competitionLicense.getStartDate(),
                competitionLicense.getEndDate());
    }
}
