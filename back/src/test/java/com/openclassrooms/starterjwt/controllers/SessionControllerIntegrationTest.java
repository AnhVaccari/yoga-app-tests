package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.hasItem;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
public class SessionControllerIntegrationTest {

     @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // Données de test
    private Session testSession;
    private User testUser;
    private Teacher testTeacher;

     @BeforeAll
     void setUpAll() {

         //System.out.println("=== SETUP SESSION INTEGRATION TESTS ===");

         // Créer un teacher
         testTeacher = new Teacher();
         testTeacher.setFirstName("John");
         testTeacher.setLastName("Doe");
         testTeacher = teacherRepository.save(testTeacher);

         // Créer un user
         testUser = new User();
         testUser.setEmail("user@example.com");
         testUser.setFirstName("Sylvie");
         testUser.setLastName("Yogi");
         testUser.setPassword("password123");
         testUser.setAdmin(false);
         testUser = userRepository.save(testUser);

         // Créer une session
         testSession = new Session();
         testSession.setName("Morning Yoga");
         testSession.setDescription("Relaxing yoga session");
         testSession.setDate(new Date());
         testSession.setTeacher(testTeacher);
         testSession.setUsers(new ArrayList<>());
         testSession = sessionRepository.save(testSession);

        //  System.out.println("Test data created:");
        //  System.out.println("- Teacher ID: " + testTeacher.getId());
        //  System.out.println("- User ID: " + testUser.getId());
        //  System.out.println("- Session ID: " + testSession.getId());
     }

     @BeforeEach
     void setUpEach() {
        // Reset des participants avant chaque test
        testSession.getUsers().clear();
        sessionRepository.save(testSession);
        
        // System.out.println("--- Reset participants before test ---");
        // System.out.println("Participants count: " + testSession.getUsers().size());
}

      @AfterAll
      void tearDownAll() {

          //System.out.println("=== CLEANUP SESSION INTEGRATION TESTS ===");

          sessionRepository.deleteAll();
          userRepository.deleteAll();
          teacherRepository.deleteAll();

          System.out.println("All test data cleaned up");
      }
    
  /*----------------------------------GET SESSION ---------------------------- */

      @Test
      @WithMockUser
      void shouldReturnSession_WhenValidId() throws Exception {

        //System.out.println("=== TEST GET SESSION - SUCCESS ===");
        //System.out.println("Session ID: " + testSession.getId());
        //System.out.println("Session name: " + testSession.getName());
        
        // When & Then - avec la vraie structure JSON
        mockMvc.perform(get("/api/session/" + testSession.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testSession.getId().intValue()))
            .andExpect(jsonPath("$.name").value(testSession.getName()))
            .andExpect(jsonPath("$.description").value(testSession.getDescription()))
            .andExpect(jsonPath("$.teacher_id").value(testTeacher.getId().intValue())) 
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.users").isEmpty());
        
        //System.out.println("Session retrieved successfully ");
}

     /*---------------------------------- SESSION NOT FOUND ---------------------------- */

      @Test
      @WithMockUser
      void shouldReturnNotFound_WhenSessionDoesNotExist() throws Exception {
          // Given - ID inexistant
          Long nonExistentId = 99999L;

          //System.out.println("=== TEST GET SESSION - NOT FOUND ===");
          //System.out.println("Non-existent ID: " + nonExistentId);

          // When & Then
          mockMvc.perform(get("/api/session/" + nonExistentId))
                  .andExpect(status().isNotFound());

          //System.out.println("404 Not Found returned as expected ");
      }

      /*---------------------------------- ALL SESSIONS ---------------------------- */

      @Test
      @WithMockUser
      void shouldReturnAllSessions() throws Exception {

          //System.out.println("=== TEST GET ALL SESSIONS ===");
          //System.out.println("Expected sessions count: at least 1 (testSession)");

          // When & Then -  MockMvc
          mockMvc.perform(get("/api/session"))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$").isArray())
                  .andExpect(jsonPath("$[*].id").value(hasItem(testSession.getId().intValue())))
                  .andExpect(jsonPath("$[*].name").value(hasItem(testSession.getName())))
                  .andExpect(jsonPath("$[*].teacher_id").value(hasItem(testTeacher.getId().intValue())));

          //System.out.println("All sessions retrieved successfully");
      }

      /*---------------------------------- CREATE SESSION ---------------------------- */
 
      @Test
      @WithMockUser
      void shouldCreateSession_WhenValidData() throws Exception {
          // Given - Préparer le DTO à envoyer
          SessionDto sessionDto = new SessionDto();
          sessionDto.setName("Evening Yoga");
          sessionDto.setDescription("Relaxing evening session");
          sessionDto.setDate(new Date());
          sessionDto.setTeacher_id(testTeacher.getId());

          String jsonContent = objectMapper.writeValueAsString(sessionDto);

          //System.out.println("=== TEST CREATE SESSION - SUCCESS ===");
          //System.out.println("Creating session: " + sessionDto.getName());
          //System.out.println("Teacher ID: " + sessionDto.getTeacher_id());

          // When & Then - POST avec JSON
          mockMvc.perform(post("/api/session")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonContent))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.name").value("Evening Yoga"))
                  .andExpect(jsonPath("$.description").value("Relaxing evening session"))
                  .andExpect(jsonPath("$.teacher_id").value(testTeacher.getId().intValue()))
                  .andExpect(jsonPath("$.id").exists()); // Un ID doit être généré

          //System.out.println("Session created successfully");

          // Vérification en base 
          assertThat(sessionRepository.findAll())
                  .hasSizeGreaterThan(1) // Au moins testSession + nouvelle session
                  .extracting(Session::getName)
                  .contains("Evening Yoga");
      }

      /*---------------------------------- PUT SESSION ---------------------------- */
       
      @Test
      @WithMockUser
      void shouldUpdateSession_WhenValidData() throws Exception {
          // Given - Préparer les nouvelles données
          SessionDto updateDto = new SessionDto();
          updateDto.setName("Updated Morning Yoga");
          updateDto.setDescription("Updated description");
          updateDto.setDate(new Date());
          updateDto.setTeacher_id(testTeacher.getId());

          String jsonContent = objectMapper.writeValueAsString(updateDto);

          // System.out.println("=== TEST UPDATE SESSION - SUCCESS ===");
          // System.out.println("Updating session ID: " + testSession.getId());
          // System.out.println("New name: " + updateDto.getName());

          // When & Then - PUT avec JSON
          mockMvc.perform(put("/api/session/" + testSession.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonContent))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.id").value(testSession.getId().intValue()))
                  .andExpect(jsonPath("$.name").value("Updated Morning Yoga"))
                  .andExpect(jsonPath("$.description").value("Updated description"))
                  .andExpect(jsonPath("$.teacher_id").value(testTeacher.getId().intValue()));

          //System.out.println("Session updated successfully");

          // Vérification en base 
          Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
          assertThat(updatedSession)
                  .isNotNull()
                  .extracting(Session::getName, Session::getDescription)
                  .containsExactly("Updated Morning Yoga", "Updated description");
      }

      /*---------------------------------- DELETE SESSION ---------------------------- */
      @Test
      @WithMockUser
      void shouldDeleteSession_WhenValidId() throws Exception {
        // Given - Créer une session temporaire pour ce test
        Session sessionToDelete = new Session();
        sessionToDelete.setName("Session to Delete");
        sessionToDelete.setDescription("Temporary session");
        sessionToDelete.setDate(new Date());
        sessionToDelete.setTeacher(testTeacher);
        sessionToDelete.setUsers(new ArrayList<>());
        sessionToDelete = sessionRepository.save(sessionToDelete);
        
        // System.out.println("=== TEST DELETE SESSION - SUCCESS ===");
        // System.out.println("Deleting session ID: " + sessionToDelete.getId());
        // System.out.println("Session name: " + sessionToDelete.getName());
        
        // When & Then
        mockMvc.perform(delete("/api/session/" + sessionToDelete.getId()))
            .andExpect(status().isOk());
        
        //System.out.println("Session deleted successfully");
        
        // Vérification la session est supprimée
        assertThat(sessionRepository.findById(sessionToDelete.getId()))
            .isEmpty();
        
        //System.out.println("Session confirmed deleted from database");
}


/*---------------------------------- PARTICIPATE SESSION ---------------------------- */
     
    @Test
    @WithMockUser
    void shouldAddUserToSession_WhenParticipate() throws Exception {

        // System.out.println("=== TEST PARTICIPATE - SUCCESS ===");
        // System.out.println("Session ID: " + testSession.getId());
        // System.out.println("User ID: " + testUser.getId());
        // System.out.println("Participants before: " + testSession.getUsers().size());

        // When & Then - POST participate
        mockMvc.perform(post("/api/session/" + testSession.getId() + "/participate/" + testUser.getId()))
                .andExpect(status().isOk());

        //System.out.println("Participate request successful");

        // Vérification en base 
        Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
        assertThat(updatedSession)
                .isNotNull()
                .extracting(Session::getUsers)
                .satisfies(users -> {
                    assertThat(users)
                            .hasSize(1)
                            .extracting(User::getId)
                            .contains(testUser.getId());
                });

        //System.out.println("User successfully added to session");
        //System.out.println("Participants after: " + updatedSession.getUsers().size());
    }
    
    /*---------------------------------- USER ALREADY PARTICIPATE ---------------------------- */

    @Test
    @WithMockUser
    void shouldReturnBadRequest_WhenUserAlreadyParticipates() throws Exception {
        // Given - Ajouter testUser à la session (il participe déjà)
        testSession.getUsers().add(testUser);
        sessionRepository.save(testSession);

        // System.out.println("=== TEST PARTICIPATE - ALREADY PARTICIPATING ===");
        // System.out.println("Session ID: " + testSession.getId());
        // System.out.println("User ID: " + testUser.getId());
        // System.out.println("User already participating: " + testSession.getUsers().size());

        // When & Then - Tentative d'ajout d'un user déjà participant
        mockMvc.perform(post("/api/session/" + testSession.getId() + "/participate/" + testUser.getId()))
                .andExpect(status().isBadRequest());

        //System.out.println("BadRequestException handled correctly");

        // Vérification qu'il n'y a toujours qu'un seul participant
        Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
        assertThat(updatedSession)
                .isNotNull()
                .extracting(Session::getUsers)
                .satisfies(users -> {
                    assertThat(users)
                            .hasSize(1) // Toujours 1, pas 2 !
                            .extracting(User::getId)
                            .contains(testUser.getId());
                });

        //System.out.println("User count unchanged (no duplicate)");
    }

    /*---------------------------------- USER UNPARTICIPATE ---------------------------- */

    @Test
    @WithMockUser
    void shouldRemoveUserFromSession_WhenNoLongerParticipate() throws Exception {
        // Given - Ajouter testUser à la session (il doit participer pour pouvoir être retiré)
        testSession.getUsers().clear(); // Reset au cas où
        testSession.getUsers().add(testUser);
        sessionRepository.save(testSession);
        
        // System.out.println("=== TEST NO LONGER PARTICIPATE - SUCCESS ===");
        // System.out.println("Session ID: " + testSession.getId());
        // System.out.println("User ID: " + testUser.getId());
        // System.out.println("Participants before: " + testSession.getUsers().size());
        
        // When & Then - DELETE participate
        mockMvc.perform(delete("/api/session/" + testSession.getId() + "/participate/" + testUser.getId()))
            .andExpect(status().isOk());
        
        //System.out.println("No longer participate request successful");
        
       // Vérification en base 
        Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
        assertThat(updatedSession)
            .isNotNull()
            .extracting(Session::getUsers)
            .satisfies(users -> {
                assertThat(users).isEmpty(); 
            });
            
        // System.out.println("User successfully removed from session");
        // System.out.println("Participants after: " + updatedSession.getUsers().size());
}
 



      

 





    
      

       



    




    

}
