package com.managementClub.managementClub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.managementClub.managementClub.repository.AppUserRepository;
import com.managementClub.managementClub.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected PasswordEncoder passwordEncoder;
    @Autowired protected PersonRepository personRepository;
    @Autowired protected AppUserRepository appUserRepository;

    @BeforeEach
    void cleanDatabase(){
        appUserRepository.deleteAll();
        personRepository.deleteAll();
    }
}
