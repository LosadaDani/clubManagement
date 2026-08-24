package com.managementClub.managementClub.config;

import com.managementClub.managementClub.model.entity.CompetitionLicense;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.DogSex;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.repository.CompetitionLicenseRepository;
import com.managementClub.managementClub.repository.DogRepository;
import com.managementClub.managementClub.repository.OrganizationRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DevDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final PersonRepository personRepository;
    private final DogRepository dogRepository;
    private final OrganizationRepository organizationRepository;
    private final CompetitionLicenseRepository competitionLicenseRepository;

    public DevDataInitializer(PersonRepository personRepository,
                              DogRepository dogRepository,
                              OrganizationRepository organizationRepository,
                              CompetitionLicenseRepository competitionLicenseRepository) {
        this.personRepository = personRepository;
        this.dogRepository = dogRepository;
        this.organizationRepository = organizationRepository;
        this.competitionLicenseRepository = competitionLicenseRepository;
    }

    @Override
    public void run(String... args) {
        if (shouldInitializeData()) {
            log.info("Initializing development data...");
            initializeOrganizations();
            initializePersons();
            initializeDogs();
            initializeCompetitionLicenses();
            log.info("Development data initialized successfully.");
        } else {
            log.info("Development data already exists. Skipping initialization.");
        }
    }

    private boolean shouldInitializeData() {
        return personRepository.count() == 0;
    }

    private void initializeOrganizations() {
        if (organizationRepository.count() > 0) {
            return;
        }

        Organization fcag = new Organization("Federació Catalana d'Agility", "FCAG");
        Organization rsce = new Organization("Real Sociedad Canina de España", "RSCE");
        Organization rfec = new Organization("Real Federación Española de Caza", "RFEC");

        organizationRepository.save(fcag);
        organizationRepository.save(rsce);
        organizationRepository.save(rfec);

        log.info("Organizations initialized: FCAG, RSCE, RFEC");
    }

    private void initializePersons() {
        if (personRepository.count() > 0) {
            return;
        }

        LocalDate memberSince = LocalDate.of(2023, 1, 1);

        Person dani = new Person(
                "Dani",
                "Losada",
                "600123456",
                "dani.losada@example.com",
                memberSince,
                MembershipStatus.ACTIVE,
                MembershipType.FULL_PARTNER
        );

        Person cristina = new Person(
                "Cristina",
                "Martínez",
                "600234567",
                "cristina.martinez@example.com",
                memberSince,
                MembershipStatus.ACTIVE,
                MembershipType.FULL_PARTNER
        );

        Person carlos = new Person(
                "Carlos",
                "López",
                "600345678",
                "carlos.lopez@example.com",
                memberSince,
                MembershipStatus.ACTIVE,
                MembershipType.SUBSCRIBED_MEMBER
        );

        Person laura = new Person(
                "Laura",
                "Sánchez",
                "600456789",
                "laura.sanchez@example.com",
                memberSince,
                MembershipStatus.INACTIVE,
                MembershipType.FULL_PARTNER
        );

        personRepository.save(dani);
        personRepository.save(cristina);
        personRepository.save(carlos);
        personRepository.save(laura);

        log.info("Persons initialized: Dani Losada, Cristina Martínez, Carlos López, Laura Sánchez");
    }

    private void initializeDogs() {
        if (dogRepository.count() > 0) {
            return;
        }

        Person dani = personRepository.findByEmail("dani.losada@example.com").orElseThrow();
        Person cristina = personRepository.findByEmail("cristina.martinez@example.com").orElseThrow();
        Person carlos = personRepository.findByEmail("carlos.lopez@example.com").orElseThrow();

        LocalDate birthDate = LocalDate.of(2022, 1, 1);

        Dog fuchur = new Dog(
                "Fuchur",
                birthDate,
                DogSex.MALE,
                "Caniche",
                "ES123456789012",
                "PED-001",
                dani
        );

        Dog gmork = new Dog(
                "Gmork",
                birthDate.plusYears(1),
                DogSex.MALE,
                "Border Collie",
                "ES123456789013",
                "PED-002",
                dani
        );

        Dog ramen = new Dog(
                "Ramen",
                birthDate.plusMonths(6),
                DogSex.FEMALE,
                "Caniche",
                "ES123456789014",
                "PED-003",
                cristina
        );

        Dog max = new Dog(
                "Max",
                birthDate.plusMonths(3),
                DogSex.MALE,
                "Border Collie",
                "ES123456789015",
                "PED-004",
                carlos
        );

        Dog luna = new Dog("Luna",
                birthDate,
                DogSex.FEMALE,
                "Golden Retriever",
                "ES999999999999",
                "PED-999",
                dani);

        dogRepository.save(fuchur);
        dogRepository.save(gmork);
        dogRepository.save(ramen);
        dogRepository.save(max);
        dogRepository.save(luna);

        log.info("Dogs initialized: Fuchur, Gmork (Dani Losada), Ramen (Cristina Martínez), Max (Carlos López)");
    }

    private void initializeCompetitionLicenses() {
        if (competitionLicenseRepository.count() > 0) {
            return;
        }

        Organization fcag = organizationRepository.findByShortName("FCAG").orElseThrow();
        Organization rsce = organizationRepository.findByShortName("RSCE").orElseThrow();
        Organization rfec = organizationRepository.findByShortName("RFEC").orElseThrow();

        Person dani = personRepository.findByEmail("dani.losada@example.com").orElseThrow();
        Person cristina = personRepository.findByEmail("cristina.martinez@example.com").orElseThrow();
        Person carlos = personRepository.findByEmail("carlos.lopez@example.com").orElseThrow();

        Dog fuchur = dogRepository.findByName("Fuchur").orElseThrow();
        Dog gmork = dogRepository.findByName("Gmork").orElseThrow();
        Dog ramen = dogRepository.findByName("Ramen").orElseThrow();
        Dog max = dogRepository.findByName("Max").orElseThrow();

        CompetitionLicense fcagFuchur = new CompetitionLicense();
        fcagFuchur.setOrganization(fcag);
        fcagFuchur.setPerson(dani);
        fcagFuchur.setDog(fuchur);
        fcagFuchur.setLicenseNumber("FCAG-2026-001");
        fcagFuchur.setStartDate(LocalDate.of(2026, 9, 1));
        fcagFuchur.setEndDate(LocalDate.of(2027, 8, 31));

        CompetitionLicense rsceFuchur = new CompetitionLicense();
        rsceFuchur.setOrganization(rsce);
        rsceFuchur.setPerson(dani);
        rsceFuchur.setDog(fuchur);
        rsceFuchur.setLicenseNumber("RSCE-2026-001");
        rsceFuchur.setStartDate(LocalDate.of(2026, 9, 1));
        rsceFuchur.setEndDate(LocalDate.of(2027, 8, 31));

        CompetitionLicense fcagGmork = new CompetitionLicense();
        fcagGmork.setOrganization(fcag);
        fcagGmork.setPerson(dani);
        fcagGmork.setDog(gmork);
        fcagGmork.setLicenseNumber("FCAG-2026-002");
        fcagGmork.setStartDate(LocalDate.of(2026, 9, 1));
        fcagGmork.setEndDate(LocalDate.of(2027, 8, 31));

        CompetitionLicense rsceRamen = new CompetitionLicense();
        rsceRamen.setOrganization(rsce);
        rsceRamen.setPerson(cristina);
        rsceRamen.setDog(ramen);
        rsceRamen.setLicenseNumber("RSCE-2026-002");
        rsceRamen.setStartDate(LocalDate.of(2026, 9, 1));
        rsceRamen.setEndDate(LocalDate.of(2027, 8, 31));

        CompetitionLicense rfecMax = new CompetitionLicense();
        rfecMax.setOrganization(rfec);
        rfecMax.setPerson(carlos);
        rfecMax.setDog(max);
        rfecMax.setLicenseNumber("RFEC-2026-001");
        rfecMax.setStartDate(LocalDate.of(2026, 1, 1));
        rfecMax.setEndDate(LocalDate.of(2026, 12, 31));

        // 1. Perro con múltiples licencias vigentes (Fuchur)
        CompetitionLicense fcagFuchurCurrent = new CompetitionLicense();
        fcagFuchurCurrent.setOrganization(fcag);
        fcagFuchurCurrent.setPerson(dani);
        fcagFuchurCurrent.setDog(fuchur);
        fcagFuchurCurrent.setLicenseNumber("FCAG-2026-005");
        fcagFuchurCurrent.setStartDate(LocalDate.of(2026, 1, 1)); // Vigente
        fcagFuchurCurrent.setEndDate(LocalDate.of(2026, 12, 31));

        CompetitionLicense rsceFuchurCurrent = new CompetitionLicense();
        rsceFuchurCurrent.setOrganization(rsce);
        rsceFuchurCurrent.setPerson(dani);
        rsceFuchurCurrent.setDog(fuchur);
        rsceFuchurCurrent.setLicenseNumber("RSCE-2026-005");
        rsceFuchurCurrent.setStartDate(LocalDate.of(2026, 6, 1)); // Vigente
        rsceFuchurCurrent.setEndDate(LocalDate.of(2027, 5, 31));

// 2. Licencia que empieza hoy (borde)
        CompetitionLicense startsToday = new CompetitionLicense();
        startsToday.setOrganization(fcag);
        startsToday.setPerson(cristina);
        startsToday.setDog(ramen);
        startsToday.setLicenseNumber("FCAG-2026-006");
        startsToday.setStartDate(LocalDate.now()); // Hoy
        startsToday.setEndDate(LocalDate.now().plusYears(1).minusDays(1));

// 3. Licencia que termina hoy (borde)
        CompetitionLicense endsToday = new CompetitionLicense();
        endsToday.setOrganization(rsce);
        endsToday.setPerson(carlos);
        endsToday.setDog(max);
        endsToday.setLicenseNumber("RSCE-2026-006");
        endsToday.setStartDate(LocalDate.now().minusYears(1).plusDays(1));
        endsToday.setEndDate(LocalDate.now()); // Hoy

        CompetitionLicense previousFcagFuchur = new CompetitionLicense();
        previousFcagFuchur.setOrganization(fcag);
        previousFcagFuchur.setPerson(dani);
        previousFcagFuchur.setDog(fuchur);
        previousFcagFuchur.setLicenseNumber("FCAG-2025-001");
        previousFcagFuchur.setStartDate(LocalDate.of(2025, 1, 1));
        previousFcagFuchur.setEndDate(LocalDate.of(2025, 12, 31));

        competitionLicenseRepository.save(fcagFuchur);
        competitionLicenseRepository.save(rsceFuchur);
        competitionLicenseRepository.save(fcagGmork);
        competitionLicenseRepository.save(rsceRamen);
        competitionLicenseRepository.save(rfecMax);
        competitionLicenseRepository.save(previousFcagFuchur);
        competitionLicenseRepository.save(fcagFuchurCurrent);
        competitionLicenseRepository.save(rsceFuchurCurrent);
        competitionLicenseRepository.save(startsToday);
        competitionLicenseRepository.save(endsToday);

        log.info("Competition licenses initialized: 6 licenses");
    }
}
