package com.managementClub.managementClub.integration;

import com.managementClub.managementClub.model.dto.LoginRequestDTO;
import com.managementClub.managementClub.model.entity.AppUser;
import com.managementClub.managementClub.model.entity.Person;
import com.managementClub.managementClub.model.enums.MembershipStatus;
import com.managementClub.managementClub.model.enums.MembershipType;
import com.managementClub.managementClub.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends AbstractIntegrationTest {

    public static final String USER_ADMIN = "testadmin";
    public static final String SECRET_123 = "secret123";

    @Test
    void login_whenCredentialsValid_returnsToken() throws Exception {
        //Given: un usuario real en la BD, con la contraseña cifrada
        createUser();

        LoginRequestDTO request =new LoginRequestDTO();
        request.setUsername(USER_ADMIN);
        request.setPassword(SECRET_123);

        //When + Then
        mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USER_ADMIN))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_whenPasswordWrong_returnsUnauthorized() throws Exception {
        //Given: un usuario real en la BD, con la contraseña cifrada
        createUser();

        LoginRequestDTO request =new LoginRequestDTO();
        request.setUsername(USER_ADMIN);
        request.setPassword("passErronea");

        //When + Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Credenciales erróneas"));
        //"Credenciales erroneas" lo genera SpringSecurity (con el locale fijo es_ES)
    }

    @Test
    void protectedRoute_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private void createUser() {
        Person person = personRepository.save(new Person(
                "Test",
                "User",
                "600000000",
                "test.user@example.com",
                LocalDate.of(2026, 1, 1),
                MembershipStatus.ACTIVE,
                MembershipType.FULL_PARTNER));

        appUserRepository.save(new AppUser(
                USER_ADMIN,
                passwordEncoder.encode(SECRET_123),
                Role.ROLE_ADMIN,
                person));
    }
}
