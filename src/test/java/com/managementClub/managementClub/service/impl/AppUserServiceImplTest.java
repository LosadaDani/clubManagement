package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.exception.ResourceAlreadyExistsException;
import com.managementClub.managementClub.exception.ResourceNotFoundException;
import com.managementClub.managementClub.mapper.AppUserMapper;
import com.managementClub.managementClub.model.dto.AppUserCreatedResponseDTO;
import com.managementClub.managementClub.model.dto.AppUserRequestDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.AppUserRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock private PersonRepository personRepository;
    @Mock private AppUserRepository appUserRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Spy private AppUserMapper appUserMapper = new AppUserMapper();

    @InjectMocks private AppUserServiceImpl service;

    private static final Long PERSON_ID = 5L;
    private static final Long MISSING_ID = 999L;
    private static final Long SAVED_USER_ID = 77L;
    private static final String USERNAME = "ana";
    private static final String HASHED_PASSWORD = "HASHED";

    private Person person() {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setName("Ana");
        person.setLastName("López");
        return person;
    }

    // La petición de alta solo lleva username y personId: no hay forma de indicar un rol
    private AppUserRequestDTO request(Long personId) {
        AppUserRequestDTO request = new AppUserRequestDTO();
        request.setUsername(USERNAME);
        request.setPersonId(personId);
        return request;
    }

    // Usuario que devuelve save: instancia distinta (otro id) de la que crea el mapper
    private AppUser savedUser(Person person) {
        AppUser savedUser = new AppUser(USERNAME, HASHED_PASSWORD, Role.ROLE_USER, person);
        savedUser.setId(SAVED_USER_ID);
        return savedUser;
    }

    //R1 registerUser Persona inexistente -> ResourceNotFound con el id en el mensaje; no se toca el repositorio de usuarios (la persona se comprueba antes que el usuario y el username) ni el codificador
    @Test
    void registerUser_whenPersonNotFound_throwsResourceNotFoundBeforeAnyUserCheck() {
        when(personRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registerUser(request(MISSING_ID)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MISSING_ID.toString());

        verifyNoInteractions(appUserRepository, passwordEncoder);
    }

    //R2 registerUser La persona ya tiene usuario -> 409 con mensaje de «ya tiene un usuario asignado»; no se consulta el username (la persona va antes), no se guarda ni se codifica
    @Test
    void registerUser_whenPersonAlreadyHasUser_throwsResourceAlreadyExistsMentioningPersonBeforeUsernameCheck() {
        Person person = person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(appUserRepository.existsByPerson(person)).thenReturn(true);

        assertThatThrownBy(() -> service.registerUser(request(PERSON_ID)))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("ya tiene un usuario asignado");

        verify(appUserRepository, never()).existsByUsername(any(String.class));
        verify(appUserRepository, never()).save(any(AppUser.class));
        verifyNoInteractions(passwordEncoder);
    }

    //R3 registerUser Username en uso (persona sin usuario) -> 409 con mensaje de «nombre de usuario» (mismo tipo que R2, se distingue por el mensaje); no se guarda ni se codifica
    @Test
    void registerUser_whenUsernameAlreadyInUse_throwsResourceAlreadyExistsMentioningUsername() {
        Person person = person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(appUserRepository.existsByPerson(person)).thenReturn(false);
        when(appUserRepository.existsByUsername(USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> service.registerUser(request(PERSON_ID)))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("nombre de usuario")
                .hasMessageNotContaining("ya tiene un usuario asignado");

        verify(appUserRepository, never()).save(any(AppUser.class));
        verifyNoInteractions(passwordEncoder);
    }

    //R4 registerUser Alta válida -> rol ROLE_USER por defecto (la petición no lleva rol); username y persona de la petición
    @Test
    void registerUser_whenValid_savesUserWithRoleUserByDefault() {
        Person person = person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(passwordEncoder.encode(any(String.class))).thenReturn(HASHED_PASSWORD);
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser(person));

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);

        service.registerUser(request(PERSON_ID));

        verify(appUserRepository).save(captor.capture());

        AppUser created = captor.getValue();
        assertThat(created.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(created.getUsername()).isEqualTo(USERNAME);
        assertThat(created.getPerson()).isSameAs(person);
    }

    //R5 registerUser Alta válida: contraseña -> el encode recibe la contraseña en claro; lo guardado es el hash que devuelve el PasswordEncoder (nunca el texto en claro); la respuesta lleva la contraseña en claro y no el hash
    @Test
    void registerUser_whenValid_savesHashedPasswordAndReturnsPlainPassword() {
        Person person = person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(passwordEncoder.encode(any(String.class))).thenReturn(HASHED_PASSWORD);
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser(person));

        ArgumentCaptor<String> plainCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);

        AppUserCreatedResponseDTO result = service.registerUser(request(PERSON_ID));

        verify(passwordEncoder).encode(plainCaptor.capture());
        verify(appUserRepository).save(userCaptor.capture());

        String plainPassword = plainCaptor.getValue();
        assertThat(plainPassword).isNotBlank();
        // Lo guardado es el hash, no el texto en claro
        assertThat(userCaptor.getValue().getPassword()).isEqualTo(HASHED_PASSWORD);
        assertThat(userCaptor.getValue().getPassword()).isNotEqualTo(plainPassword);
        // La respuesta lleva la contraseña en claro que recibió el codificador, no el hash
        assertThat(result.getPassword()).isEqualTo(plainPassword);
        assertThat(result.getPassword()).isNotEqualTo(HASHED_PASSWORD);
    }

    //R6 registerUser Alta válida: respuesta -> id, username, rol y resumen de la persona salen de lo que devuelve save
    @Test
    void registerUser_whenValid_returnsResponseBuiltFromSavedUser() {
        Person person = person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(passwordEncoder.encode(any(String.class))).thenReturn(HASHED_PASSWORD);
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser(person));

        AppUserCreatedResponseDTO result = service.registerUser(request(PERSON_ID));

        assertThat(result.getId()).isEqualTo(SAVED_USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(result.getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.getPerson().getName()).isEqualTo("Ana");
        assertThat(result.getPerson().getLastName()).isEqualTo("López");
    }

    //R7 registerUser Dos altas consecutivas -> las contraseñas en claro generadas son distintas («aleatoria»).
    // PROBABILÍSTICO: la contraseña son los 12 primeros caracteres de un UUID (11 hexadecimales y un guion, 44 bits aleatorios), así que dos altas coinciden con probabilidad 2^-44 (~5,7e-14): en la práctica no es un test inestable, pero no es determinista.
    @Test
    void registerUser_whenCalledTwice_generatesDifferentPlainPasswords() {
        Person person = person();

        when(personRepository.findById(PERSON_ID)).thenReturn(Optional.of(person));
        when(passwordEncoder.encode(any(String.class))).thenReturn(HASHED_PASSWORD);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<String> plainCaptor = ArgumentCaptor.forClass(String.class);

        AppUserCreatedResponseDTO first = service.registerUser(request(PERSON_ID));
        AppUserCreatedResponseDTO second = service.registerUser(request(PERSON_ID));

        verify(passwordEncoder, times(2)).encode(plainCaptor.capture());
        assertThat(plainCaptor.getAllValues().get(0)).isNotEqualTo(plainCaptor.getAllValues().get(1));
        assertThat(first.getPassword()).isNotEqualTo(second.getPassword());
    }
}
