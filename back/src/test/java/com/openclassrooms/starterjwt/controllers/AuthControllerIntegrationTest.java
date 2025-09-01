package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur pour les tests
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setAdmin(false);

        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    /*---------------- LOGIN SUCCESS ----------------*/
    @Test
    void shouldAuthenticateUserSuccessfully() throws Exception {
        String json = "{ \"email\": \"" + testUser.getEmail() + "\", \"password\": \"password123\" }";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.admin").value(false))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    /*---------------- LOGIN FAIL (INVALID EMAIL) ----------------*/
    @Test
    void shouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        String json = "{ \"email\": \"wrong@example.com\", \"password\": \"password123\" }";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    /*---------------- LOGIN FAIL (WRONG PASSWORD) ----------------*/
    @Test
    void shouldReturnUnauthorized_WhenWrongPassword() throws Exception {
        String json = "{ \"email\": \"" + testUser.getEmail() + "\", \"password\": \"wrongpass\" }";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    /*---------------- REGISTER FAIL (EMAIL ALREADY TAKEN) ----------------*/
    @Test
    void shouldReturnBadRequest_WhenEmailAlreadyTaken() throws Exception {
        String json = "{ \"email\": \"" + testUser.getEmail()
                + "\", \"firstName\": \"New\", \"lastName\": \"User\", \"password\": \"pass123\" }";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    /*---------------- REGISTER FAIL (INVALID REQUEST) ----------------*/
    @Test
    void shouldReturnBadRequest_WhenRegisterRequestInvalid() throws Exception {
        // Email mal formé
        String jsonInvalidEmail = "{ \"email\": \"invalidemail\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"password\": \"password123\" }";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalidEmail))
                .andExpect(status().isBadRequest());

        // Mot de passe trop court
        String jsonShortPassword = "{ \"email\": \"new@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"password\": \"123\" }";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonShortPassword))
                .andExpect(status().isBadRequest());

        // Nom vide
        String jsonEmptyLastName = "{ \"email\": \"new2@example.com\", \"firstName\": \"John\", \"lastName\": \"\", \"password\": \"password123\" }";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonEmptyLastName))
                .andExpect(status().isBadRequest());
    }

}
