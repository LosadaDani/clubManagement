package com.managementClub.managementClub.security;

import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock private AppUserRepository appUserRepository;

    @InjectMocks private AppUserDetailsService service;

    private static final String USERNAME = "ana";
    private static final String MISSING_USERNAME = "nadie";
    private static final String HASHED_PASSWORD = "HASHED";

    private AppUser appUserWithRole(Role role) {
        return new AppUser(USERNAME, HASHED_PASSWORD, role, null);
    }

    //U-1 loadUserByUsername username inexistente -> UsernameNotFoundException (no se fija el texto, ver D-S3 del plan)
    @Test
    void loadUserByUsername_whenUsernameNotFound_throwsUsernameNotFound() {
        when(appUserRepository.findByUsername(MISSING_USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername(MISSING_USERNAME))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    //U-2 loadUserByUsername username existente -> AppUserDetails que envuelve el mismo AppUser, con su username y su contraseña (el hash guardado)
    @Test
    void loadUserByUsername_whenUsernameExists_returnsAppUserDetailsWrappingSameUser() {
        AppUser appUser = appUserWithRole(Role.ROLE_USER);

        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(appUser));

        UserDetails result = service.loadUserByUsername(USERNAME);

        assertThat(result).isInstanceOf(AppUserDetails.class);
        assertThat(((AppUserDetails) result).getAppUser()).isSameAs(appUser);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getPassword()).isEqualTo(HASHED_PASSWORD);
    }

    //U-3 Autoridad del usuario -> exactamente una, igual al nombre del rol (ROLE_USER, ROLE_ADMIN, ROLE_TRAINER): es la que Spring compara en hasRole(...)
    @ParameterizedTest
    @EnumSource(Role.class)
    void loadUserByUsername_whenUsernameExists_hasSingleAuthorityNamedAsRole(Role role) {
        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(appUserWithRole(role)));

        UserDetails result = service.loadUserByUsername(USERNAME);

        assertThat(result.getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .containsExactly(role.name());
    }
}
