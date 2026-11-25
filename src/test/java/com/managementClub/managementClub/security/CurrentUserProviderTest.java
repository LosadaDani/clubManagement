package com.managementClub.managementClub.security;

import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// El usuario actual se simula con SecurityContextHolder (estado estático): se limpia tras cada test.
@ExtendWith(MockitoExtension.class)
class CurrentUserProviderTest {

    @Mock private AppUserRepository appUserRepository;

    @InjectMocks private CurrentUserProvider provider;

    private static final String USERNAME = "ana";
    private static final String MISSING_USERNAME = "nadie";
    // Ids por encima de 127: los Long.valueOf(...) de estos valores son objetos distintos cada vez (fuera de la caché de Long),
    // así que una comparación por referencia (==) en lugar de equals() daría false aunque los valores coincidan
    private static final long OWN_PERSON_ID = 1000L;
    private static final long OTHER_PERSON_ID = 2000L;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of()));
    }

    // Usuario USERNAME con el rol indicado y la persona OWN_PERSON_ID
    private AppUser appUserWithRole(Role role) {
        Person person = new Person();
        person.setId(Long.valueOf(OWN_PERSON_ID));
        return new AppUser(USERNAME, "HASHED", role, person);
    }

    private void currentUserIs(AppUser appUser) {
        authenticateAs(USERNAME);
        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(appUser));
    }

    //G1 getCurrentAppUser Usuario autenticado en el contexto -> devuelve el AppUser que el repositorio asocia a ese username
    @Test
    void getCurrentAppUser_whenUserInContext_returnsRepositoryUser() {
        AppUser appUser = appUserWithRole(Role.ROLE_USER);
        currentUserIs(appUser);

        assertThat(provider.getCurrentAppUser()).isSameAs(appUser);
    }

    //G2 getCurrentAppUser CARACTERIZACIÓN (pendiente en BACKLOG.md): username del contexto inexistente en el repositorio -> NoSuchElementException (orElseThrow sin mensaje, acabaría en un 500)
    @Test
    void getCurrentAppUser_whenUsernameNotFound_throwsNoSuchElementException() {
        authenticateAs(MISSING_USERNAME);
        when(appUserRepository.findByUsername(MISSING_USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> provider.getCurrentAppUser())
                .isInstanceOf(NoSuchElementException.class);
    }

    //A1 isAdmin true solo para ROLE_ADMIN
    @ParameterizedTest
    @CsvSource({"ROLE_ADMIN,true", "ROLE_USER,false", "ROLE_TRAINER,false"})
    void isAdmin_isTrueOnlyForAdminRole(Role role, boolean expected) {
        currentUserIs(appUserWithRole(role));

        assertThat(provider.isAdmin()).isEqualTo(expected);
    }

    //O1 isAdminOrOwner ADMIN -> true con cualquier id (propio, ajeno) y con null (recurso inexistente: no hay propietario)
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {OWN_PERSON_ID, OTHER_PERSON_ID})
    void isAdminOrOwner_whenAdmin_isTrueForAnyId(Long personId) {
        currentUserIs(appUserWithRole(Role.ROLE_ADMIN));

        assertThat(provider.isAdminOrOwner(personId)).isTrue();
    }

    //O2 isAdminOrOwner No ADMIN con el id de su propia persona -> true
    @ParameterizedTest
    @EnumSource(value = Role.class, names = "ROLE_ADMIN", mode = EnumSource.Mode.EXCLUDE)
    void isAdminOrOwner_whenNotAdminAndOwnPersonId_isTrue(Role role) {
        currentUserIs(appUserWithRole(role));

        assertThat(provider.isAdminOrOwner(Long.valueOf(OWN_PERSON_ID))).isTrue();
    }

    //O3 isAdminOrOwner No ADMIN con el id de otra persona -> false
    @ParameterizedTest
    @EnumSource(value = Role.class, names = "ROLE_ADMIN", mode = EnumSource.Mode.EXCLUDE)
    void isAdminOrOwner_whenNotAdminAndOtherPersonId_isFalse(Role role) {
        currentUserIs(appUserWithRole(role));

        assertThat(provider.isAdminOrOwner(Long.valueOf(OTHER_PERSON_ID))).isFalse();
    }

    //O4 isAdminOrOwner No ADMIN con null -> false. Es el mecanismo del «403 antes que 404»: cuando el recurso no existe los Services pasan null y un no ADMIN no puede distinguirlo de uno ajeno
    @ParameterizedTest
    @EnumSource(value = Role.class, names = "ROLE_ADMIN", mode = EnumSource.Mode.EXCLUDE)
    void isAdminOrOwner_whenNotAdminAndNullId_isFalse(Role role) {
        currentUserIs(appUserWithRole(role));

        assertThat(provider.isAdminOrOwner(null)).isFalse();
    }
}
