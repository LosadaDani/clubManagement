package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.PersonMapper;
import com.managementClub.managementClub.model.dto.PersonRequestDTO;
import com.managementClub.managementClub.model.dto.PersonResponseDTO;
import com.managementClub.managementClub.model.dto.PersonStatusDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.PersonRepository;
import com.managementClub.managementClub.security.CurrentUserProvider;
import com.managementClub.managementClub.service.ReceiptLineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    @Mock private PersonRepository personRepository;
    @Mock private ReceiptLineService receiptLineService;
    @Mock private CurrentUserProvider currentUserProvider;
    @Spy private PersonMapper personMapper = new PersonMapper();
    @Spy private Clock clock = Clock.fixed(
            Instant.parse("2026-06-15T10:00:00Z"), ZoneId.of("Europe/Madrid"));

    @InjectMocks private PersonServiceImpl service;

    private static final Long PERSON_ID = 5L;
    private static final Long SAVED_PERSON_ID = 6L;
    private static final Long MISSING_ID = 999L;
    private static final String EMAIL = "ana@club.com";
    private static final String NEW_EMAIL = "nueva@club.com";
    private static final LocalDate TODAY = LocalDate.of(2026, 6, 15);
    private static final LocalDate MEMBER_SINCE = LocalDate.of(2020, 1, 10);
    private static final LocalDate NEW_MEMBER_SINCE = LocalDate.of(2019, 5, 5);

    private AppUser appUserWithRole(Role role) {
        AppUser appUser = new AppUser();
        appUser.setRole(role);
        return appUser;
    }

    private PersonRequestDTO createRequest(MembershipType type) {
        PersonRequestDTO request = new PersonRequestDTO();
        request.setName("Ana");
        request.setLastName("López");
        request.setTelephone("600000000");
        request.setEmail(EMAIL);
        request.setMembershipType(type);
        return request;
    }

    // Persona existente: FULL_PARTNER, ACTIVE, email EMAIL, memberSince MEMBER_SINCE
    private Person existingPerson() {
        Person person = new Person("Ana", "López", "600000000", EMAIL, MEMBER_SINCE,
                MembershipStatus.ACTIVE, MembershipType.FULL_PARTNER);
        person.setId(PERSON_ID);
        return person;
    }

    // Petición de modificación con todos los datos distintos a los de existingPerson()
    private PersonRequestDTO updateRequest(MembershipType type, String email) {
        PersonRequestDTO request = new PersonRequestDTO();
        request.setName("Nombre nuevo");
        request.setLastName("Apellido nuevo");
        request.setTelephone("611111111");
        request.setEmail(email);
        request.setMembershipType(type);
        return request;
    }

    //C1 createPerson ENTRENADOR con tipo que no es de formación -> AccessDenied y no se toca nada
    @ParameterizedTest
    @EnumSource(value = MembershipType.class,
            names = {"INITIATION_TRAINING", "PERMANENT_TRAINING"}, mode = EnumSource.Mode.EXCLUDE)
    void createPerson_whenTrainerAndTypeNotTraining_throwsAccessDenied(MembershipType type) {
        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_TRAINER));

        assertThatThrownBy(() -> service.createPerson(createRequest(type)))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(personRepository, receiptLineService);
    }

    //C2 createPerson ENTRENADOR con INITIATION_TRAINING o PERMANENT_TRAINING -> se crea
    @ParameterizedTest
    @EnumSource(value = MembershipType.class, names = {"INITIATION_TRAINING", "PERMANENT_TRAINING"})
    void createPerson_whenTrainerAndTypeTraining_savesPerson(MembershipType type) {
        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_TRAINER));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.createPerson(createRequest(type));

        verify(personRepository).save(any(Person.class));
        assertThat(result.getMembershipType()).isEqualTo(type);
    }

    //C3 createPerson ADMIN con cualquier tipo -> se crea
    @ParameterizedTest
    @EnumSource(MembershipType.class)
    void createPerson_whenAdmin_savesPersonWithAnyType(MembershipType type) {
        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_ADMIN));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.createPerson(createRequest(type));

        verify(personRepository).save(any(Person.class));
        assertThat(result.getMembershipType()).isEqualTo(type);
    }

    //C4 createPerson Email ya existente -> ResourceAlreadyExists, no se guarda ni se crean líneas
    @Test
    void createPerson_whenEmailAlreadyExists_throwsResourceAlreadyExists() {
        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_ADMIN));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Person()));

        assertThatThrownBy(() -> service.createPerson(createRequest(MembershipType.INITIATION_TRAINING)))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(personRepository, never()).save(any(Person.class));
        verifyNoInteractions(receiptLineService);
    }

    //C5 createPerson Alta válida -> se guardan los datos del DTO y estado ACTIVE
    @Test
    void createPerson_whenValid_savesPersonActiveWithRequestData() {
        PersonRequestDTO request = createRequest(MembershipType.FULL_PARTNER);
        request.setMemberSince(MEMBER_SINCE);

        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_ADMIN));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

        PersonResponseDTO result = service.createPerson(request);

        verify(personRepository).save(captor.capture());

        Person saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Ana");
        assertThat(saved.getLastName()).isEqualTo("López");
        assertThat(saved.getTelephone()).isEqualTo("600000000");
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getMembershipType()).isEqualTo(MembershipType.FULL_PARTNER);
        assertThat(saved.getMembershipStatus()).isEqualTo(MembershipStatus.ACTIVE);

        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getMembershipStatus()).isEqualTo(MembershipStatus.ACTIVE);
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.FULL_PARTNER);
    }

    //C6 createPerson Alta sin memberSince -> se asigna la fecha del Clock (2026-06-15)
    @Test
    void createPerson_whenMemberSinceNull_assignsTodayFromClock() {
        PersonRequestDTO request = createRequest(MembershipType.FULL_PARTNER);

        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_ADMIN));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

        PersonResponseDTO result = service.createPerson(request);

        verify(personRepository).save(captor.capture());

        assertThat(captor.getValue().getMemberSince()).isEqualTo(TODAY);
        assertThat(result.getMemberSince()).isEqualTo(TODAY);
    }

    //C7 createPerson Alta con memberSince informada -> se conserva, no se sustituye por hoy
    @Test
    void createPerson_whenMemberSinceGiven_keepsGivenDate() {
        PersonRequestDTO request = createRequest(MembershipType.FULL_PARTNER);
        request.setMemberSince(MEMBER_SINCE);

        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_ADMIN));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

        PersonResponseDTO result = service.createPerson(request);

        verify(personRepository).save(captor.capture());

        assertThat(captor.getValue().getMemberSince()).isEqualTo(MEMBER_SINCE);
        assertThat(result.getMemberSince()).isEqualTo(MEMBER_SINCE);
    }

    //C8+C10 createPerson INITIATION_TRAINING -> createInitiationLines con la persona DEVUELTA por save y DTO de esa persona
    @Test
    void createPerson_whenInitiationTraining_createsLinesForSavedPersonAndReturnsItsDto() {
        // save devuelve una instancia distinta (otro id) de la que crea el mapper
        Person savedPerson = new Person();
        savedPerson.setId(SAVED_PERSON_ID);

        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_ADMIN));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        PersonResponseDTO result = service.createPerson(createRequest(MembershipType.INITIATION_TRAINING));

        verify(receiptLineService).createInitiationLines(same(savedPerson));
        assertThat(result.getId()).isEqualTo(SAVED_PERSON_ID);
    }

    //C9 createPerson Cualquier otro tipo -> no se crean líneas de iniciación
    @ParameterizedTest
    @EnumSource(value = MembershipType.class, names = "INITIATION_TRAINING", mode = EnumSource.Mode.EXCLUDE)
    void createPerson_whenNotInitiationTraining_doesNotCreateInitiationLines(MembershipType type) {
        when(currentUserProvider.getCurrentAppUser()).thenReturn(appUserWithRole(Role.ROLE_ADMIN));
        when(personRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.createPerson(createRequest(type));

        verifyNoInteractions(receiptLineService);
    }

    //G1 getPersonById Ni admin ni propietario, id inexistente -> AccessDenied (403 antes que 404) y no se consulta el repositorio
    @Test
    void getPersonById_whenNotAdminNorOwner_throwsAccessDeniedWithoutQueryingRepository() {
        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.getPersonById(MISSING_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(personRepository);
    }

    //G2 getPersonById Admin/propietario, persona inexistente -> ResourceNotFound
    @Test
    void getPersonById_whenPersonNotFound_throwsResourceNotFound() {
        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(true);
        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPersonById(MISSING_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    //G3 getPersonById Admin/propietario, persona existente -> DTO con todos los campos
    @Test
    void getPersonById_whenPersonExists_returnsMappedDto() {
        Person person = existingPerson();

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));

        PersonResponseDTO result = service.getPersonById(PERSON_ID);

        assertThat(result.getId()).isEqualTo(PERSON_ID);
        assertThat(result.getName()).isEqualTo("Ana");
        assertThat(result.getLastName()).isEqualTo("López");
        assertThat(result.getTelephone()).isEqualTo("600000000");
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getMemberSince()).isEqualTo(MEMBER_SINCE);
        assertThat(result.getMembershipStatus()).isEqualTo(MembershipStatus.ACTIVE);
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.FULL_PARTNER);
    }

    //S1 searchPersons CARACTERIZACIÓN (sin regla documentada, ver plan D7): el texto llega normalizado al repositorio (trim y espacios internos colapsados)
    @ParameterizedTest
    @CsvSource({
            "'  ana   lopez ', 'ana lopez'",
            "'ana  lopez', 'ana lopez'",
            "'ana', 'ana'"
    })
    void searchPersons_whenTextHasExtraSpaces_searchesWithNormalizedText(String searchText, String normalizedText) {
        when(personRepository.searchByFullName(normalizedText)).thenReturn(List.of());

        service.searchPersons(searchText);

        verify(personRepository).searchByFullName(normalizedText);
    }

    //S2 searchPersons Con resultados -> lista mapeada, mismo tamaño y orden que el repositorio
    @Test
    void searchPersons_whenResults_returnsMappedPersonsInOrder() {
        Person first = existingPerson();
        Person second = existingPerson();
        second.setId(SAVED_PERSON_ID);
        second.setName("Ana María");

        when(personRepository.searchByFullName("ana")).thenReturn(List.of(first, second));

        List<PersonResponseDTO> result = service.searchPersons("ana");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(PERSON_ID);
        assertThat(result.get(1).getId()).isEqualTo(SAVED_PERSON_ID);
        assertThat(result.get(1).getName()).isEqualTo("Ana María");
    }

    //A1 getAllPersons Con varias personas -> todas mapeadas, mismo orden que findAll
    @Test
    void getAllPersons_whenSeveralPersons_returnsAllMappedInOrder() {
        Person first = existingPerson();
        Person second = existingPerson();
        second.setId(SAVED_PERSON_ID);

        when(personRepository.findAll()).thenReturn(List.of(first, second));

        List<PersonResponseDTO> result = service.getAllPersons();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(PERSON_ID);
        assertThat(result.get(1).getId()).isEqualTo(SAVED_PERSON_ID);
    }

    //U1 updatePerson Ni admin ni propietario, id inexistente -> AccessDenied (403 antes que 404) y no se consulta el repositorio
    @Test
    void updatePerson_whenNotAdminNorOwner_throwsAccessDeniedWithoutQueryingRepository() {
        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.updatePerson(MISSING_ID, new PersonRequestDTO()))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(personRepository);
    }

    //U2 updatePerson Admin/propietario, persona inexistente -> ResourceNotFound y no se guarda
    @Test
    void updatePerson_whenPersonNotFound_throwsResourceNotFound() {
        when(currentUserProvider.isAdminOrOwner(MISSING_ID)).thenReturn(true);
        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePerson(MISSING_ID, new PersonRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(personRepository, never()).save(any(Person.class));
    }

    //U3 updatePerson No admin cambia su tipo de membresía -> AccessDenied; no se guarda, no se consulta el email y la entidad no cambia
    @ParameterizedTest
    @EnumSource(value = MembershipType.class, names = "FULL_PARTNER", mode = EnumSource.Mode.EXCLUDE)
    void updatePerson_whenNotAdminChangesMembershipType_throwsAccessDenied(MembershipType newType) {
        Person existing = existingPerson();

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(false);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.updatePerson(PERSON_ID, updateRequest(newType, NEW_EMAIL)))
                .isInstanceOf(AccessDeniedException.class);

        verify(personRepository, never()).save(any(Person.class));
        verify(personRepository, never()).findByEmail(any(String.class));
        assertThat(existing.getMembershipType()).isEqualTo(MembershipType.FULL_PARTNER);
        assertThat(existing.getName()).isEqualTo("Ana");
    }

    //U4 updatePerson Admin cambia el tipo de membresía -> se permite y se guarda
    @Test
    void updatePerson_whenAdminChangesMembershipType_updatesType() {
        Person existing = existingPerson();

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.updatePerson(PERSON_ID, updateRequest(MembershipType.SUBSCRIBED_MEMBER, EMAIL));

        verify(personRepository).save(existing);
        assertThat(existing.getMembershipType()).isEqualTo(MembershipType.SUBSCRIBED_MEMBER);
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.SUBSCRIBED_MEMBER);
    }

    //U5 updatePerson No admin modifica sus datos sin cambiar el tipo -> se permite y se actualizan los datos
    @Test
    void updatePerson_whenNotAdminKeepsMembershipType_updatesData() {
        Person existing = existingPerson();

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(false);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.updatePerson(PERSON_ID, updateRequest(MembershipType.FULL_PARTNER, EMAIL));

        verify(personRepository).save(existing);
        assertThat(existing.getName()).isEqualTo("Nombre nuevo");
        assertThat(existing.getLastName()).isEqualTo("Apellido nuevo");
        assertThat(existing.getTelephone()).isEqualTo("611111111");
        assertThat(existing.getMembershipType()).isEqualTo(MembershipType.FULL_PARTNER);
        assertThat(result.getName()).isEqualTo("Nombre nuevo");
    }

    //U6 updatePerson Email nuevo que ya tiene otra persona -> ResourceAlreadyExists y no se guarda
    @Test
    void updatePerson_whenNewEmailBelongsToAnotherPerson_throwsResourceAlreadyExists() {
        Person existing = existingPerson();

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.findByEmail(NEW_EMAIL)).thenReturn(Optional.of(new Person()));

        assertThatThrownBy(() -> service.updatePerson(PERSON_ID, updateRequest(MembershipType.FULL_PARTNER, NEW_EMAIL)))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(personRepository, never()).save(any(Person.class));
    }

    //U7 updatePerson Email nuevo libre -> se consulta, se guarda y el email queda actualizado
    @Test
    void updatePerson_whenNewEmailIsFree_updatesEmail() {
        Person existing = existingPerson();

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.findByEmail(NEW_EMAIL)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.updatePerson(PERSON_ID, updateRequest(MembershipType.FULL_PARTNER, NEW_EMAIL));

        verify(personRepository).findByEmail(NEW_EMAIL);
        verify(personRepository).save(existing);
        assertThat(existing.getEmail()).isEqualTo(NEW_EMAIL);
        assertThat(result.getEmail()).isEqualTo(NEW_EMAIL);
    }

    //U8 updatePerson El email solo cambia en mayúsculas -> no se consulta el repositorio (la persona no choca consigo misma) y se guarda
    @Test
    void updatePerson_whenEmailDiffersOnlyInCase_doesNotCheckUniquenessAndSaves() {
        Person existing = existingPerson();

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.updatePerson(PERSON_ID, updateRequest(MembershipType.FULL_PARTNER, EMAIL.toUpperCase()));

        verify(personRepository, never()).findByEmail(any(String.class));
        verify(personRepository).save(existing);
        assertThat(existing.getEmail()).isEqualTo(EMAIL.toUpperCase());
    }

    //U9 updatePerson memberSince no enviada -> se conserva la que tenía la persona
    @Test
    void updatePerson_whenMemberSinceNull_keepsExistingMemberSince() {
        Person existing = existingPerson();
        PersonRequestDTO request = updateRequest(MembershipType.FULL_PARTNER, EMAIL);

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.updatePerson(PERSON_ID, request);

        assertThat(existing.getMemberSince()).isEqualTo(MEMBER_SINCE);
        assertThat(result.getMemberSince()).isEqualTo(MEMBER_SINCE);
    }

    //U10 updatePerson memberSince enviada -> se actualiza
    @Test
    void updatePerson_whenMemberSinceGiven_updatesMemberSince() {
        Person existing = existingPerson();
        PersonRequestDTO request = updateRequest(MembershipType.FULL_PARTNER, EMAIL);
        request.setMemberSince(NEW_MEMBER_SINCE);

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.updatePerson(PERSON_ID, request);

        assertThat(existing.getMemberSince()).isEqualTo(NEW_MEMBER_SINCE);
        assertThat(result.getMemberSince()).isEqualTo(NEW_MEMBER_SINCE);
    }

    //U11 updatePerson El PUT no cambia el estado de membresía (persona INACTIVE sigue INACTIVE)
    @Test
    void updatePerson_whenValid_doesNotChangeMembershipStatus() {
        Person existing = existingPerson();
        existing.setMembershipStatus(MembershipStatus.INACTIVE);

        when(currentUserProvider.isAdminOrOwner(PERSON_ID)).thenReturn(true);
        when(currentUserProvider.isAdmin()).thenReturn(true);
        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.updatePerson(PERSON_ID, updateRequest(MembershipType.FULL_PARTNER, EMAIL));

        assertThat(existing.getMembershipStatus()).isEqualTo(MembershipStatus.INACTIVE);
        assertThat(result.getMembershipStatus()).isEqualTo(MembershipStatus.INACTIVE);
    }

    //M1 changeMembershipStatus Persona inexistente -> ResourceNotFound y no se guarda
    @Test
    void changeMembershipStatus_whenPersonNotFound_throwsResourceNotFound() {
        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeMembershipStatus(MISSING_ID, new PersonStatusDTO(MembershipStatus.INACTIVE)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(personRepository, never()).save(any(Person.class));
    }

    //M2 changeMembershipStatus Estado distinto del actual -> la entidad cambia, se guarda y el DTO refleja el nuevo estado
    @ParameterizedTest
    @CsvSource({"ACTIVE,INACTIVE", "INACTIVE,ACTIVE", "ACTIVE,CANCELLED"})
    void changeMembershipStatus_whenStatusDiffers_updatesAndSaves(MembershipStatus from, MembershipStatus to) {
        Person existing = existingPerson();
        existing.setMembershipStatus(from);

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDTO result = service.changeMembershipStatus(PERSON_ID, new PersonStatusDTO(to));

        verify(personRepository).save(existing);
        assertThat(existing.getMembershipStatus()).isEqualTo(to);
        assertThat(result.getMembershipStatus()).isEqualTo(to);
    }

    //M3 changeMembershipStatus CARACTERIZACIÓN (sin regla documentada, ver plan D5): mismo estado -> no se guarda y se devuelve la persona tal cual
    @ParameterizedTest
    @EnumSource(MembershipStatus.class)
    void changeMembershipStatus_whenStatusSame_doesNotSave(MembershipStatus status) {
        Person existing = existingPerson();
        existing.setMembershipStatus(status);

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(existing));

        PersonResponseDTO result = service.changeMembershipStatus(PERSON_ID, new PersonStatusDTO(status));

        verify(personRepository, never()).save(any(Person.class));
        assertThat(result.getId()).isEqualTo(PERSON_ID);
        assertThat(result.getMembershipStatus()).isEqualTo(status);
    }
}
