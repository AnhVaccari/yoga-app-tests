package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc

public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("password123");
        testUser.setAdmin(false);

        testUser = userRepository.save(testUser);

        // System.out.println("=== SETUP INTEGRATION TESTS ===");
        // System.out.println("Test user created with ID: " + testUser.getId());
    }

    @AfterEach
    void tearDown() {

        // System.out.println("=== CLEANUP INTEGRATION TESTS ===");

        userRepository.deleteAll();

        // System.out.println("All test data cleaned up");
    }

    /*---------------------------------- USER EXIST ---------------------------- */

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnUser_WhenValidId() throws Exception {

        System.out.println("=== TEST GET USER - SUCCESS ===");
        System.out.println("User ID: " + testUser.getId());

        // When & Then - MockMvc
        mockMvc.perform(get("/api/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()));

        System.out.println("Test passed with MockMvc");
    }

    /*---------------------------------- USER INEXISTANT ---------------------------- */

    @Test
    @WithMockUser
    void shouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Given - ID inexistant
        Long nonExistentId = 99999L;

        System.out.println("=== TEST GET USER - NOT FOUND ===");
        System.out.println("Non-existent ID: " + nonExistentId);

        // When & Then - MockMvc
        mockMvc.perform(get("/api/user/" + nonExistentId))
                .andExpect(status().isNotFound());

        System.out.println("404 Not Found returned as expected");
    }

    /*---------------------------------- USER (INVALID ID) ---------------------------- */

    @Test
    @WithMockUser
    void shouldReturnBadRequest_WhenInvalidId() throws Exception {
        // Given - ID invalide (pas un nombre)
        String invalidId = "invalid-id";

        System.out.println("=== TEST GET USER - INVALID ID ===");
        System.out.println("Invalid ID: " + invalidId);

        // When & Then - MockMvc
        mockMvc.perform(get("/api/user/" + invalidId))
                .andExpect(status().isBadRequest());

        System.out.println("400 Bad Request returned as expected");
    }

    /*---------------------------------- DELETE USER ---------------------------- */

    @Test
    // Même email que testUser
    @WithMockUser(username = "test@example.com")
    void shouldDeleteUser_WhenUserDeletesHimself() throws Exception {

        // System.out.println("=== TEST DELETE USER - SUCCESS ===");
        // System.out.println("User deleting himself: " + testUser.getEmail());
        // System.out.println("User ID: " + testUser.getId());

        // When & Then - L'utilisateur peut se supprimer lui-même
        mockMvc.perform(delete("/api/user/" + testUser.getId()))
                .andExpect(status().isOk());

        // Vérification que l'utilisateur a été supprimé
        assertThat(userRepository.findById(testUser.getId()))
                .isEmpty();

        // System.out.println("User successfully deleted");
    }

    /*---------------------------------- DELETE USER (UNAUTHORIZED) ---------------------------- */

    @Test
    // Email différent
    @WithMockUser(username = "different@example.com")
    void shouldReturnUnauthorized_WhenUserTriesToDeleteAnotherUser() throws Exception {

        // Given - Créer un autre utilisateur pour ce test
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setFirstName("Sylvie");
        anotherUser.setLastName("Yogi");
        anotherUser.setPassword("password456");
        anotherUser.setAdmin(false);
        anotherUser = userRepository.save(anotherUser);

        System.out.println("=== TEST DELETE USER - UNAUTHORIZED ===");
        System.out.println("Authenticated user: different@example.com");
        System.out.println("Trying to delete user: " + anotherUser.getEmail());

        // When & Then - Tentative de suppression d'un autre utilisateur
        mockMvc.perform(delete("/api/user/" + anotherUser.getId()))
                .andExpect(status().isUnauthorized());

        // Vérification que l'utilisateur n'a PAS été supprimé
        assertThat(userRepository.findById(anotherUser.getId()))
                .isPresent() // L'utilisateur existe encore
                .get()
                .extracting(User::getEmail)
                .isEqualTo(anotherUser.getEmail());

        System.out.println("Unauthorized access blocked as expected");

        // Cleanup de ce test
        userRepository.deleteById(anotherUser.getId());
    }

}
