package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.InvalidBusinessRuleException;
import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.CompetitionLicenseMapper;
import com.managementClub.managementClub.model.dto.CompetitionLicenseRequestDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseResponseDTO;
import com.managementClub.managementClub.model.dto.CompetitionLicenseUpdateRequestDTO;
import com.managementClub.managementClub.model.entity.CompetitionLicense;
import com.managementClub.managementClub.model.entity.Dog;
import com.managementClub.managementClub.model.entity.Organization;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.repository.CompetitionLicenseRepository;
import com.managementClub.managementClub.repository.DogRepository;
import com.managementClub.managementClub.repository.OrganizationRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionLicenseServiceImplTest {

    @Mock private CompetitionLicenseRepository competitionLicenseRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private PersonRepository personRepository;
    @Mock private DogRepository dogRepository;
    @Mock private CurrentUserProvider currentUserProvider;
    @Spy private CompetitionLicenseMapper competitionLicenseMapper = new CompetitionLicenseMapper();

    @InjectMocks private CompetitionLicenseServiceImpl service;

    private static final Long LICENSE_ID = 20L;
    private static final Long OTHER_LICENSE_ID = 21L;
    private static final Long SAVED_LICENSE_ID = 77L;
    private static final Long DOG_ID = 10L;
    private static final Long OWNER_ID = 5L;
    private static final Long OTHER_PERSON_ID = 6L;
    private static final Long ORGANIZATION_ID = 3L;
    private static final Long MISSING_ID = 999L;
    private static final String LICENSE_NUMBER = "LC-2026-001";
    private static final String NEW_LICENSE_NUMBER = "LC-2027-777";
    private static final LocalDate START_DATE = LocalDate.of(2026, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2026, 12, 31);
    private static final LocalDate NEW_START_DATE = LocalDate.of(2027, 2, 1);
    private static final LocalDate NEW_END_DATE = LocalDate.of(2027, 11, 30);

    private Person person(Long id) {
        Person person = new Person();
        person.setId(id);
        person.setName("Ana");
        person.setLastName("López");
        return person;
    }

    private Dog dogOf(Person owner) {
        Dog dog = new Dog();
        dog.setId(DOG_ID);
        dog.setName("Rex");
        dog.setBreed("Labrador");
        dog.setMicrochip("123456789012345");
        dog.setOwner(owner);
        return dog;
    }

    private Organization organization() {
        Organization organization = new Organization("Real Sociedad Canina de España", "RSCE");
        organization.setId(ORGANIZATION_ID);
        return organization;
    }

    // Petición de alta: organización ORGANIZATION_ID, perro DOG_ID y persona la indicada
    private CompetitionLicenseRequestDTO request(Long personId, LocalDate startDate, LocalDate endDate) {
        CompetitionLicenseRequestDTO request = new CompetitionLicenseRequestDTO();
        request.setOrganizationId(ORGANIZATION_ID);
        request.setPersonId(personId);
        request.setDogId(DOG_ID);
        request.setLicenseNumber(LICENSE_NUMBER);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return request;
    }

    // Licencia existente: START_DATE - END_DATE, LICENSE_NUMBER
    private CompetitionLicense existingLicense(Organization organization, Person person, Dog dog) {
        CompetitionLicense license = new CompetitionLicense(organization, person, dog, LICENSE_NUMBER, START_DATE, END_DATE);
        license.setId(LICENSE_ID);
        return license;
    }

    private CompetitionLicenseUpdateRequestDTO updateRequest(LocalDate startDate, LocalDate endDate) {
        CompetitionLicenseUpdateRequestDTO request = new CompetitionLicenseUpdateRequestDTO();
        request.setLicenseNumber(NEW_LICENSE_NUMBER);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return request;
    }

    // Los dos métodos de consulta comparten comprobaciones: se prueban con la misma operación parametrizada
    static Stream<Arguments> licenseQueries() {
        BiFunction<CompetitionLicenseServiceImpl, Long, List<CompetitionLicenseResponseDTO>> all =
                CompetitionLicenseServiceImpl::getCompetitionLicensesByDogId;
        BiFunction<CompetitionLicenseServiceImpl, Long, List<CompetitionLicenseResponseDTO>> current =
                CompetitionLicenseServiceImpl::getLicenseCurrentByDogId;
        return Stream.of(
                Arguments.of(Named.of("getCompetitionLicensesByDogId", all)),
                Arguments.of(Named.of("getLicenseCurrentByDogId", current)));
    }

    // ---------------- createCompetitionLicense ----------------
    // Orden documentado: permiso (403) -> organización (404) -> persona y perro (404) -> propietario (400) -> fechas (400) -> solapamiento (409).
    // Cada test de error incluye también las violaciones posteriores de la cadena, para que el orden se fije test a test.
    // El Service siempre consulta antes el perro (necesita su propietario para decidir el permiso).

    //C1 createCompetitionLicense USER con persona ajena (perro suyo) -> AccessDenied antes de organización, persona y licencias
    @Test
    void createCompetitionLicense_whenPersonNotOwn_throwsAccessDeniedBeforeAnyOtherLookup() {
        // Violaciones posteriores presentes: organización y persona no existen, propietario no coincide, fechas invertidas
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dogOf(person(OWNER_ID))));
        when(currentUserProvider.isAdminOrOwner(OTHER_PERSON_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OTHER_PERSON_ID, END_DATE, START_DATE)))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(organizationRepository, personRepository, competitionLicenseRepository);
    }

    //C2 createCompetitionLicense USER con persona propia pero perro ajeno -> AccessDenied antes de organización, persona y licencias
    @Test
    void createCompetitionLicense_whenDogNotOwn_throwsAccessDeniedBeforeAnyOtherLookup() {
        // Violaciones posteriores presentes: organización y persona no existen, propietario no coincide, fechas invertidas
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dogOf(person(OTHER_PERSON_ID))));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(currentUserProvider.isAdminOrOwner(OTHER_PERSON_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OWNER_ID, END_DATE, START_DATE)))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(organizationRepository, personRepository, competitionLicenseRepository);
    }

    //C3 createCompetitionLicense USER con persona propia y perro inexistente -> AccessDenied (403, no 404)
    @Test
    void createCompetitionLicense_whenDogNotFoundAndUserNotAdmin_throwsAccessDeniedNotNotFound() {
        // Violaciones posteriores presentes: organización y persona no existen, el perro no existe, fechas invertidas
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.empty());
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(false);

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OWNER_ID, END_DATE, START_DATE)))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(organizationRepository, personRepository, competitionLicenseRepository);
    }

    //C4 createCompetitionLicense ADMIN, organización inexistente -> 404 de organización antes que persona, propietario, fechas y solapamiento
    @Test
    void createCompetitionLicense_whenOrganizationNotFound_throwsNotFoundMentioningOrganizationBeforeLaterChecks() {
        // Violaciones posteriores presentes: la persona no existe, el propietario del perro no coincide, fechas invertidas
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dogOf(person(OWNER_ID))));
        when(currentUserProvider.isAdminOrOwner(OTHER_PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OTHER_PERSON_ID, END_DATE, START_DATE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organización");

        verifyNoInteractions(personRepository, competitionLicenseRepository);
    }

    //C4b createCompetitionLicense ADMIN, organización y perro inexistentes -> gana el 404 de organización (no el de perro)
    @Test
    void createCompetitionLicense_whenOrganizationAndDogNotFound_throwsNotFoundMentioningOrganization() {
        // Violaciones posteriores presentes: la persona no existe, el perro no existe, fechas invertidas
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(true);
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OWNER_ID, END_DATE, START_DATE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organización");

        verifyNoInteractions(personRepository, competitionLicenseRepository);
    }

    //C5 createCompetitionLicense ADMIN, persona inexistente -> 404 de persona antes que propietario (400), fechas y solapamiento
    @Test
    void createCompetitionLicense_whenPersonNotFound_throwsNotFoundMentioningPersonBeforeLaterChecks() {
        // Violaciones posteriores presentes: el propietario del perro no coincide con la persona, fechas invertidas
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dogOf(person(OWNER_ID))));
        when(currentUserProvider.isAdminOrOwner(OTHER_PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(personRepository.findById(OTHER_PERSON_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OTHER_PERSON_ID, END_DATE, START_DATE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Persona");

        verifyNoInteractions(competitionLicenseRepository);
    }

    //C6 createCompetitionLicense ADMIN, perro inexistente -> 404 de perro antes que fechas y solapamiento
    @Test
    void createCompetitionLicense_whenDogNotFoundAndUserAdmin_throwsNotFoundMentioningDogBeforeLaterChecks() {
        // Violaciones posteriores presentes: fechas invertidas
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(true);
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(person(OWNER_ID)));

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OWNER_ID, END_DATE, START_DATE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Perro");

        verifyNoInteractions(competitionLicenseRepository);
    }

    //C7 createCompetitionLicense ADMIN con persona distinta del propietario del perro -> 400 de propietario (se aplica también a ADMIN) antes que fechas y solapamiento
    @Test
    void createCompetitionLicense_whenPersonIsNotDogOwner_throwsInvalidBusinessRuleMentioningOwnerBeforeLaterChecks() {
        // Violaciones posteriores presentes: fechas invertidas
        Dog dog = dogOf(person(OWNER_ID));

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dog));
        when(currentUserProvider.isAdminOrOwner(OTHER_PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(personRepository.findById(OTHER_PERSON_ID)).thenReturn(Optional.of(person(OTHER_PERSON_ID)));

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OTHER_PERSON_ID, END_DATE, START_DATE)))
                .isInstanceOf(InvalidBusinessRuleException.class)
                .hasMessageContaining("propietario");

        verifyNoInteractions(competitionLicenseRepository);
    }

    //C8 createCompetitionLicense Fechas con inicio posterior a fin -> 400 de fechas antes que el solapamiento (no se consulta el solapamiento)
    @Test
    void createCompetitionLicense_whenStartDateAfterEndDate_throwsInvalidBusinessRuleMentioningDatesBeforeOverlap() {
        Person person = person(OWNER_ID);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dogOf(person)));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(person));

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OWNER_ID, END_DATE, START_DATE)))
                .isInstanceOf(InvalidBusinessRuleException.class)
                .hasMessageContaining("fecha");

        verifyNoInteractions(competitionLicenseRepository);
    }

    //C9 createCompetitionLicense Fechas con inicio igual a fin -> no lanza, se consulta el solapamiento y se guarda
    @Test
    void createCompetitionLicense_whenStartDateEqualsEndDate_savesLicense() {
        Person person = person(OWNER_ID);
        Organization organization = organization();
        Dog dog = dogOf(person);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dog));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(person));
        when(competitionLicenseRepository.existsByOrganizationAndPersonAndDogAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                organization, person, dog, START_DATE, START_DATE)).thenReturn(false);
        when(competitionLicenseRepository.save(any(CompetitionLicense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<CompetitionLicense> captor = ArgumentCaptor.forClass(CompetitionLicense.class);

        assertThatCode(() -> service.createCompetitionLicense(request(OWNER_ID, START_DATE, START_DATE)))
                .doesNotThrowAnyException();

        verify(competitionLicenseRepository).save(captor.capture());
        assertThat(captor.getValue().getStartDate()).isEqualTo(captor.getValue().getEndDate());
    }

    //C12 createCompetitionLicense Solapamiento -> 409 y no se guarda; el repositorio se consulta con (organización, persona, perro, fin nueva, inicio nueva)
    @Test
    void createCompetitionLicense_whenOverlappingLicenseExists_throwsResourceAlreadyExists() {
        Person person = person(OWNER_ID);
        Organization organization = organization();
        Dog dog = dogOf(person);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dog));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(person));
        // Fechas distintas entre sí: el orden (fin, inicio) de los dos últimos argumentos importa
        when(competitionLicenseRepository.existsByOrganizationAndPersonAndDogAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                organization, person, dog, END_DATE, START_DATE)).thenReturn(true);

        assertThatThrownBy(() -> service.createCompetitionLicense(request(OWNER_ID, START_DATE, END_DATE)))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(competitionLicenseRepository, never()).save(any(CompetitionLicense.class));
    }

    //C13 createCompetitionLicense Alta válida -> lo guardado lleva organización, persona y perro de los repositorios y los datos del DTO; la respuesta sale de lo que devuelve save
    @Test
    void createCompetitionLicense_whenValid_savesLicenseWithRelatedEntitiesAndReturnsSavedLicense() {
        Person person = person(OWNER_ID);
        Organization organization = organization();
        Dog dog = dogOf(person);
        // save devuelve una instancia distinta (otro id) de la que crea el mapper
        CompetitionLicense savedLicense = existingLicense(organization, person, dog);
        savedLicense.setId(SAVED_LICENSE_ID);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dog));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(personRepository.findById(OWNER_ID)).thenReturn(Optional.of(person));
        when(competitionLicenseRepository.existsByOrganizationAndPersonAndDogAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                organization, person, dog, END_DATE, START_DATE)).thenReturn(false);
        when(competitionLicenseRepository.save(any(CompetitionLicense.class))).thenReturn(savedLicense);

        ArgumentCaptor<CompetitionLicense> captor = ArgumentCaptor.forClass(CompetitionLicense.class);

        CompetitionLicenseResponseDTO result = service.createCompetitionLicense(request(OWNER_ID, START_DATE, END_DATE));

        verify(competitionLicenseRepository).save(captor.capture());

        CompetitionLicense created = captor.getValue();
        assertThat(created.getOrganization()).isSameAs(organization);
        assertThat(created.getPerson()).isSameAs(person);
        assertThat(created.getDog()).isSameAs(dog);
        assertThat(created.getLicenseNumber()).isEqualTo(LICENSE_NUMBER);
        assertThat(created.getStartDate()).isEqualTo(START_DATE);
        assertThat(created.getEndDate()).isEqualTo(END_DATE);

        assertThat(result.getId()).isEqualTo(SAVED_LICENSE_ID);
    }

    // ---------------- getCompetitionLicensesByDogId y getLicenseCurrentByDogId ----------------

    //L1 consultas por perro Perro inexistente y usuario no admin -> AccessDenied (403, no 404) y no se consultan licencias
    @ParameterizedTest(name = "{0}")
    @MethodSource("licenseQueries")
    void licenseQueries_whenDogNotFoundAndUserNotAdmin_throwAccessDenied(
            BiFunction<CompetitionLicenseServiceImpl, Long, List<CompetitionLicenseResponseDTO>> query) {
        when(dogRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(false);

        assertThatThrownBy(() -> query.apply(service, MISSING_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(competitionLicenseRepository);
    }

    //L2 consultas por perro Perro inexistente y admin -> ResourceNotFound con el id en el mensaje
    @ParameterizedTest(name = "{0}")
    @MethodSource("licenseQueries")
    void licenseQueries_whenDogNotFoundAndUserAdmin_throwResourceNotFound(
            BiFunction<CompetitionLicenseServiceImpl, Long, List<CompetitionLicenseResponseDTO>> query) {
        when(dogRepository.findById(MISSING_ID)).thenReturn(Optional.empty());
        // El perro no existe: el Service no tiene propietario y pregunta por permisos con null
        when(currentUserProvider.isAdminOrOwner(isNull())).thenReturn(true);

        assertThatThrownBy(() -> query.apply(service, MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());

        verifyNoInteractions(competitionLicenseRepository);
    }

    //L3 consultas por perro Perro de otro propietario y usuario no admin -> AccessDenied; el permiso se evalúa con el id del propietario del perro
    @ParameterizedTest(name = "{0}")
    @MethodSource("licenseQueries")
    void licenseQueries_whenDogNotOwnedByUser_throwAccessDenied(
            BiFunction<CompetitionLicenseServiceImpl, Long, List<CompetitionLicenseResponseDTO>> query) {
        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dogOf(person(OWNER_ID))));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> query.apply(service, DOG_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(competitionLicenseRepository);
    }

    //L4 getCompetitionLicensesByDogId Perro propio con licencias -> lista mapeada en el mismo orden, consultada con el perro devuelto por findById; no se piden las vigentes
    @Test
    void getCompetitionLicensesByDogId_whenDogOwned_returnsMappedLicensesInOrder() {
        Person person = person(OWNER_ID);
        Organization organization = organization();
        Dog dog = dogOf(person);
        CompetitionLicense first = existingLicense(organization, person, dog);
        CompetitionLicense second = existingLicense(organization, person, dog);
        second.setId(OTHER_LICENSE_ID);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dog));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(competitionLicenseRepository.findByDog(dog)).thenReturn(List.of(first, second));

        List<CompetitionLicenseResponseDTO> result = service.getCompetitionLicensesByDogId(DOG_ID);

        verify(competitionLicenseRepository, never()).findLicenseCurrentByDog(any(Dog.class));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(LICENSE_ID);
        assertThat(result.get(1).getId()).isEqualTo(OTHER_LICENSE_ID);
        assertThat(result.get(0).getLicenseNumber()).isEqualTo(LICENSE_NUMBER);
        assertThat(result.get(0).getOrganization().getShortName()).isEqualTo("RSCE");
        assertThat(result.get(0).getDog().getMicrochip()).isEqualTo("123456789012345");
    }

    //L5 getLicenseCurrentByDogId Perro propio con licencias vigentes -> lista mapeada, consultada con el perro devuelto por findById; no se piden todas
    @Test
    void getLicenseCurrentByDogId_whenDogOwned_returnsMappedCurrentLicensesInOrder() {
        Person person = person(OWNER_ID);
        Organization organization = organization();
        Dog dog = dogOf(person);
        CompetitionLicense first = existingLicense(organization, person, dog);
        CompetitionLicense second = existingLicense(organization, person, dog);
        second.setId(OTHER_LICENSE_ID);

        when(dogRepository.findById(DOG_ID)).thenReturn(Optional.of(dog));
        when(currentUserProvider.isAdminOrOwner(OWNER_ID)).thenReturn(true);
        when(competitionLicenseRepository.findLicenseCurrentByDog(dog)).thenReturn(List.of(first, second));

        List<CompetitionLicenseResponseDTO> result = service.getLicenseCurrentByDogId(DOG_ID);

        verify(competitionLicenseRepository, never()).findByDog(any(Dog.class));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(LICENSE_ID);
        assertThat(result.get(1).getId()).isEqualTo(OTHER_LICENSE_ID);
    }

    // ---------------- updateCompetitionLicense ----------------
    // Orden documentado: licencia existente (404) -> fechas (400) -> solapamiento (409).

    //X1 updateCompetitionLicense Licencia inexistente (con fechas invertidas) -> 404 de licencia antes que fechas y solapamiento
    @Test
    void updateCompetitionLicense_whenLicenseNotFound_throwsNotFoundBeforeLaterChecks() {
        // Violaciones posteriores presentes: fechas invertidas
        when(competitionLicenseRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCompetitionLicense(MISSING_ID, updateRequest(NEW_END_DATE, NEW_START_DATE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());

        verify(competitionLicenseRepository, never()).existsOverlappingLicenseExcludingId(
                any(Organization.class), any(Person.class), any(Dog.class), any(LocalDate.class), any(LocalDate.class), any(Long.class));
        verify(competitionLicenseRepository, never()).save(any(CompetitionLicense.class));
    }

    //X2 updateCompetitionLicense Fechas con inicio posterior a fin -> 400 de fechas antes que el solapamiento; no se guarda y la licencia no cambia
    @Test
    void updateCompetitionLicense_whenStartDateAfterEndDate_throwsInvalidBusinessRuleBeforeOverlap() {
        Person person = person(OWNER_ID);
        CompetitionLicense existing = existingLicense(organization(), person, dogOf(person));

        when(competitionLicenseRepository.findById(LICENSE_ID)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.updateCompetitionLicense(LICENSE_ID, updateRequest(NEW_END_DATE, NEW_START_DATE)))
                .isInstanceOf(InvalidBusinessRuleException.class)
                .hasMessageContaining("fecha");

        verify(competitionLicenseRepository, never()).existsOverlappingLicenseExcludingId(
                any(Organization.class), any(Person.class), any(Dog.class), any(LocalDate.class), any(LocalDate.class), any(Long.class));
        verify(competitionLicenseRepository, never()).save(any(CompetitionLicense.class));
        assertThat(existing.getLicenseNumber()).isEqualTo(LICENSE_NUMBER);
        assertThat(existing.getStartDate()).isEqualTo(START_DATE);
    }

    //X3 updateCompetitionLicense Fechas con inicio igual a fin -> no lanza y se guarda
    @Test
    void updateCompetitionLicense_whenStartDateEqualsEndDate_savesLicense() {
        Organization organization = organization();
        Person person = person(OWNER_ID);
        Dog dog = dogOf(person);
        CompetitionLicense existing = existingLicense(organization, person, dog);

        when(competitionLicenseRepository.findById(LICENSE_ID)).thenReturn(Optional.of(existing));
        when(competitionLicenseRepository.existsOverlappingLicenseExcludingId(
                organization, person, dog, NEW_START_DATE, NEW_START_DATE, LICENSE_ID)).thenReturn(false);
        when(competitionLicenseRepository.save(any(CompetitionLicense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> service.updateCompetitionLicense(LICENSE_ID, updateRequest(NEW_START_DATE, NEW_START_DATE)))
                .doesNotThrowAnyException();

        verify(competitionLicenseRepository).save(existing);
        assertThat(existing.getStartDate()).isEqualTo(existing.getEndDate());
    }

    //X4 updateCompetitionLicense Solapamiento con otra licencia -> 409; no se guarda y la licencia no cambia. La consulta recibe organización, persona y perro de la licencia existente, fin y inicio nuevos y el id propio
    @Test
    void updateCompetitionLicense_whenOverlappingLicenseExists_throwsResourceAlreadyExistsAndDoesNotChangeLicense() {
        Organization organization = organization();
        Person person = person(OWNER_ID);
        Dog dog = dogOf(person);
        CompetitionLicense existing = existingLicense(organization, person, dog);

        when(competitionLicenseRepository.findById(LICENSE_ID)).thenReturn(Optional.of(existing));
        // Fechas distintas entre sí: el orden (fin, inicio) de los dos argumentos de fecha importa
        when(competitionLicenseRepository.existsOverlappingLicenseExcludingId(
                organization, person, dog, NEW_END_DATE, NEW_START_DATE, LICENSE_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.updateCompetitionLicense(LICENSE_ID, updateRequest(NEW_START_DATE, NEW_END_DATE)))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(competitionLicenseRepository, never()).save(any(CompetitionLicense.class));
        assertThat(existing.getLicenseNumber()).isEqualTo(LICENSE_NUMBER);
        assertThat(existing.getStartDate()).isEqualTo(START_DATE);
        assertThat(existing.getEndDate()).isEqualTo(END_DATE);
    }

    //X5 updateCompetitionLicense Corrección válida -> número y fechas nuevos; organización, persona y perro no cambian; se guarda esa licencia y la respuesta sale de lo que devuelve save
    @Test
    void updateCompetitionLicense_whenValid_updatesOnlyNumberAndDatesAndReturnsSavedLicense() {
        Organization organization = organization();
        Person person = person(OWNER_ID);
        Dog dog = dogOf(person);
        CompetitionLicense existing = existingLicense(organization, person, dog);
        // save devuelve una instancia distinta (otro id) de la licencia existente
        CompetitionLicense savedLicense = existingLicense(organization, person, dog);
        savedLicense.setId(SAVED_LICENSE_ID);

        when(competitionLicenseRepository.findById(LICENSE_ID)).thenReturn(Optional.of(existing));
        when(competitionLicenseRepository.existsOverlappingLicenseExcludingId(
                organization, person, dog, NEW_END_DATE, NEW_START_DATE, LICENSE_ID)).thenReturn(false);
        when(competitionLicenseRepository.save(any(CompetitionLicense.class))).thenReturn(savedLicense);

        CompetitionLicenseResponseDTO result = service.updateCompetitionLicense(LICENSE_ID, updateRequest(NEW_START_DATE, NEW_END_DATE));

        verify(competitionLicenseRepository).save(existing);
        assertThat(existing.getLicenseNumber()).isEqualTo(NEW_LICENSE_NUMBER);
        assertThat(existing.getStartDate()).isEqualTo(NEW_START_DATE);
        assertThat(existing.getEndDate()).isEqualTo(NEW_END_DATE);
        assertThat(existing.getOrganization()).isSameAs(organization);
        assertThat(existing.getPerson()).isSameAs(person);
        assertThat(existing.getDog()).isSameAs(dog);

        assertThat(result.getId()).isEqualTo(SAVED_LICENSE_ID);
    }
}
