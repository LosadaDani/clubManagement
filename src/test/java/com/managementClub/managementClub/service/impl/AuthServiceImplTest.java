package com.managementClub.managementClub.service.impl;

import com.managementClub.managementClub.mapper.AppUserMapper;
import com.managementClub.managementClub.model.dto.LoginRequestDTO;
import com.managementClub.managementClub.model.dto.LoginResponseDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.Role;
import com.managementClub.managementClub.security.AppUserDetails;
import com.managementClub.managementClub.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Spy private AppUserMapper appUserMapper = new AppUserMapper();

    @InjectMocks private AuthServiceImpl service;

    private static final Long PERSON_ID = 5L;
    private static final String USERNAME = "ana";
    private static final String PASSWORD = "secreto123";
    private static final String HASHED_PASSWORD = "HASHED";
    private static final String TOKEN = "TOKEN.JWT.FIRMADO";

    private LoginRequestDTO request() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        return request;
    }

    private AppUser appUserWithRole(Role role) {
        Person person = new Person();
        person.setId(PERSON_ID);
        person.setName("Ana");
        person.setLastName("López");
        return new AppUser(USERNAME, HASHED_PASSWORD, role, person);
    }

    // Resultado de una autenticación correcta: el principal es el AppUserDetails que construye AppUserDetailsService
    private Authentication authenticated(AppUser appUser) {
        return new UsernamePasswordAuthenticationToken(new AppUserDetails(appUser), null);
    }

    //T1 login Credenciales correctas -> se autentica con el username y la contraseña del DTO; el token se genera para el usuario autenticado (el del principal); la respuesta lleva username, rol, resumen de persona y ese token
    @Test
    void login_whenCredentialsCorrect_authenticatesGeneratesTokenAndReturnsResponse() {
        AppUser appUser = appUserWithRole(Role.ROLE_USER);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated(appUser));
        when(jwtService.generateToken(any(AppUser.class))).thenReturn(TOKEN);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);

        LoginResponseDTO result = service.login(request());

        verify(authenticationManager).authenticate(captor.capture());
        // El token se pide para el usuario autenticado (el del principal), no para otro
        verify(jwtService).generateToken(same(appUser));

        Authentication sent = captor.getValue();
        assertThat(sent).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(sent.getPrincipal()).isEqualTo(USERNAME);
        assertThat(sent.getCredentials()).isEqualTo(PASSWORD);

        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(result.getPerson().getId()).isEqualTo(PERSON_ID);
        assertThat(result.getPerson().getName()).isEqualTo("Ana");
        assertThat(result.getPerson().getLastName()).isEqualTo("López");
        assertThat(result.getToken()).isEqualTo(TOKEN);
    }

    //T2 login Credenciales incorrectas (authenticate lanza BadCredentialsException) -> se propaga la misma excepción y no se emite ningún token
    @Test
    void login_whenCredentialsWrong_propagatesBadCredentialsAndDoesNotGenerateToken() {
        BadCredentialsException badCredentials = new BadCredentialsException("Credenciales erróneas");

        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(badCredentials);

        assertThatThrownBy(() -> service.login(request()))
                .isSameAs(badCredentials);

        verifyNoInteractions(jwtService);
    }

    //T3 login El rol del usuario autenticado llega a la respuesta, para cada rol
    @ParameterizedTest
    @EnumSource(Role.class)
    void login_whenCredentialsCorrect_returnsRoleOfAuthenticatedUser(Role role) {
        AppUser appUser = appUserWithRole(role);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated(appUser));
        when(jwtService.generateToken(any(AppUser.class))).thenReturn(TOKEN);

        LoginResponseDTO result = service.login(request());

        assertThat(result.getRole()).isEqualTo(role);
    }
}
