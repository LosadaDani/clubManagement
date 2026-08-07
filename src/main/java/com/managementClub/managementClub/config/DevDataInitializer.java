package com.managementClub.managementClub.config;

import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.DogSex;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
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

    public DevDataInitializer(PersonRepository personRepository,
                              DogRepository dogRepository,
                              OrganizationRepository organizationRepository) {
        this.personRepository = personRepository;
        this.dogRepository = dogRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public void run(String... args) {
        if (shouldInitializeData()) {
            log.info("Initializing development data...");
            initializeOrganizations();
            initializePersons();
            initializeDogs();
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

        dogRepository.save(fuchur);
        dogRepository.save(gmork);
        dogRepository.save(ramen);
        dogRepository.save(max);

        log.info("Dogs initialized: Fuchur, Gmork (Dani Losada), Ramen (Cristina Martínez), Max (Carlos López)");
    }
}
