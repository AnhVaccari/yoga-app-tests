package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;

import com.openclassrooms.starterjwt.repository.TeacherRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
public class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher testTeacher1;
    private Teacher testTeacher2;

    @BeforeEach
    void setUp() {
        // Create test teachers (don't delete existing ones as they might be referenced)
        testTeacher1 = new Teacher();
        testTeacher1.setFirstName("John");
        testTeacher1.setLastName("Doe");
        testTeacher1 = teacherRepository.save(testTeacher1);

        testTeacher2 = new Teacher();
        testTeacher2.setFirstName("Anna");
        testTeacher2.setLastName("Smith");
        testTeacher2 = teacherRepository.save(testTeacher2);
    }

    @AfterEach
    void tearDown() {
        // Don't delete teachers as they might be referenced by sessions
        // Let @Transactional handle cleanup
    }

    /*---------------- GET TEACHER ----------------*/
    @Test
    @WithMockUser
    void shouldReturnTeacher_WhenValidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teacher/" + testTeacher1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTeacher1.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    /*---------------- TEACHER DOES NOT EXIST ----------------*/
    @Test
    @WithMockUser
    void shouldReturnNotFound_WhenTeacherDoesNotExist() throws Exception {
        // Given
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(get("/api/teacher/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    /*---------------- ERRORS ----------------*/
    @Test
    @WithMockUser
    void shouldReturnBadRequest_WhenInvalidId() throws Exception {
        // Given
        String invalidId = "invalid-id";

        // When & Then
        mockMvc.perform(get("/api/teacher/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    /*---------------- GET ALL TEACHERS ----------------*/
    @Test
    @WithMockUser
    void shouldReturnAllTeachers() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].firstName").value(hasItem("John")))
                .andExpect(jsonPath("$[*].firstName").value(hasItem("Anna")));
    }

}
