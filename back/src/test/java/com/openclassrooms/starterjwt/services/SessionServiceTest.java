package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    /*-------------------------- CREATE SESSION ------------------------------ */

    @Test
    void shouldReturnSavedSession_WhenValidData() {
        // Given - Préparation des données
        Session inputSession = new Session();
        inputSession.setName("Yoga Session");
        inputSession.setDescription("Morning yoga");

        Session savedSession = new Session();
        savedSession.setId(1L);
        savedSession.setName("Yoga Session");
        savedSession.setDescription("Morning yoga");

        // System.out.println("=== TEST CREATE SESSION ===");
        // System.out.println("Session à créer: " + inputSession.getName());

        // Configuration du mock
        when(sessionRepository.save(inputSession)).thenReturn(savedSession);

        // When - Exécution
        Session result = sessionService.create(inputSession);

        // Then - Vérifications
        assertNotNull(result);
        assertEquals(savedSession.getId(), result.getId());
        assertEquals(savedSession.getName(), result.getName());
        assertEquals(savedSession.getDescription(), result.getDescription());

        // System.out.println("Session créée avec ID: " + result.getId());

        // Vérification de l'appel au repository
        verify(sessionRepository, times(1)).save(inputSession);
    }

    /*-------------------------- DELETE SESSION ------------------------------ */

    @Test
    void shouldDeleteSession_WhenValidId() {
        // Given - Préparation des données
        Long sessionId = 1L;

        // System.out.println("=== TEST DELETE SESSION ===");
        // System.out.println("Session ID à supprimer: " + sessionId);

        // When - Exécution
        sessionService.delete(sessionId);

        // System.out.println("Méthode delete() appelée");

        // Then - Vérification que le repository a bien été appelé
        verify(sessionRepository, times(1)).deleteById(sessionId);
        // System.out.println("Vérification: deleteById(" + sessionId);
    }

    /*-------------------------- FIND ALL SESSIONS ------------------------------ */

    @Test
    void shouldReturnListOfSessions_WhenSessionsExist() {
        // Given - Préparation des données
        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Morning Yoga");
        session1.setDescription("Relaxing session");

        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Evening Yoga");
        session2.setDescription("Intensive session");

        List<Session> expectedSessions = Arrays.asList(session1, session2);

        // System.out.println("=== TEST FIND ALL SESSIONS ===");
        // System.out.println("Nombre de sessions attendues: " +
        // expectedSessions.size());

        // Configuration du mock
        when(sessionRepository.findAll()).thenReturn(expectedSessions);

        // When - Exécution
        List<Session> actualSessions = sessionService.findAll();

        // Then - Vérifications
        assertNotNull(actualSessions);
        assertEquals(2, actualSessions.size());
        assertEquals(expectedSessions.get(0).getId(), actualSessions.get(0).getId());
        assertEquals(expectedSessions.get(0).getName(), actualSessions.get(0).getName());
        assertEquals(expectedSessions.get(1).getId(), actualSessions.get(1).getId());
        assertEquals(expectedSessions.get(1).getName(), actualSessions.get(1).getName());

        // System.out.println("Nombre de sessions retournées: " +
        // actualSessions.size());

        // Vérification de l'appel au repository
        verify(sessionRepository, times(1)).findAll();
    }

    /*-------------------------- GET SESSION ------------------------------ */

    @Test
    void shouldReturnSession_WhenSessionExists() {
        // Given - Préparation des données
        Long sessionId = 1L;
        Session expectedSession = new Session();
        expectedSession.setId(sessionId);
        expectedSession.setName("Yoga Session");
        expectedSession.setDescription("Morning yoga");

        // System.out.println("=== TEST GET BY ID - SUCCESS ===");
        // System.out.println("Session ID recherché: " + sessionId);
        // System.out.println("Session attendue: " + expectedSession.getName());

        // Configuration du mock
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(expectedSession));

        // When - Exécution
        Session actualSession = sessionService.getById(sessionId);

        // Then - Vérifications
        assertNotNull(actualSession);
        assertEquals(expectedSession.getId(), actualSession.getId());
        assertEquals(expectedSession.getName(), actualSession.getName());
        assertEquals(expectedSession.getDescription(), actualSession.getDescription());

        // System.out.println("Session trouvée: " + actualSession.getName());

        // Vérification de l'appel au repository
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    /*-------------------------- SESSION NOT FOUND ------------------------------ */

    @Test
    void shouldReturnNull_WhenSessionNotExists() {
        // Given - Préparation des données
        Long sessionId = 999L;

        // System.out.println("=== TEST GET BY ID - NOT FOUND ===");
        // System.out.println("Session ID recherché (inexistant): " + sessionId);

        // Configuration du mock - retourne Optional.empty()
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When - Exécution
        Session actualSession = sessionService.getById(sessionId);

        // Then - Vérifications
        assertNull(actualSession);

        // System.out.println("Résultat: null comme attendu");

        // Vérification de l'appel au repository
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    /*-------------------------- UPDATE SESSION ------------------------------ */

    @Test
    void shouldReturnUpdatedSession_WhenValidData() {
        // Given - Préparation des données
        Long sessionId = 1L;
        Session inputSession = new Session();
        inputSession.setName("Updated Yoga");
        inputSession.setDescription("Updated description");

        Session savedSession = new Session();
        savedSession.setId(sessionId);
        savedSession.setName("Updated Yoga");
        savedSession.setDescription("Updated description");

        // System.out.println("=== TEST UPDATE SESSION ===");
        // System.out.println("Session ID à mettre à jour: " + sessionId);
        // System.out.println("Nouveau nom: " + inputSession.getName());

        // Configuration du mock
        when(sessionRepository.save(inputSession)).thenReturn(savedSession);

        // When - Exécution
        Session result = sessionService.update(sessionId, inputSession);

        // Then - Vérifications
        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        assertEquals(inputSession.getName(), result.getName());
        assertEquals(inputSession.getDescription(), result.getDescription());

        // Vérification que l'ID a été assigné avant save
        assertEquals(sessionId, inputSession.getId());

        // System.out.println("Session mise à jour avec succès");

        // Vérification de l'appel au repository
        verify(sessionRepository, times(1)).save(inputSession);
    }

    /*-------------------------- PARTICIPATE SESSION (SUCCES) ------------------------------ */

    @Test
    void shouldAddUserToSession_WhenValidSessionAndUser() {
        // Given - Préparation des données
        Long sessionId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Session session = new Session();
        session.setId(sessionId);
        session.setName("Yoga Session");

        // Liste vide au début
        session.setUsers(new ArrayList<>());

        Session savedSession = new Session();
        savedSession.setId(sessionId);
        savedSession.setName("Yoga Session");

        // System.out.println("=== TEST PARTICIPATE - SUCCESS ===");
        // System.out.println("Session ID: " + sessionId + ", User ID: " + userId);
        // System.out.println("Participants avant: " + session.getUsers().size());

        // Configuration des mocks
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.save(session)).thenReturn(savedSession);

        // When - Exécution
        sessionService.participate(sessionId, userId);

        // Then - Vérifications
        assertEquals(1, session.getUsers().size());
        assertTrue(session.getUsers().contains(user));

        // System.out.println("Participants après: " + session.getUsers().size());

        // Vérifications des appels aux repositories
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).save(session);
    }

    /*-------------------------- SESSION NOT FOUND (PARTICIPATE) ------------------------------ */

    @Test
    void shouldThrowNotFoundException_WhenSessionNotExists_OnParticipate() {
        // Given - Préparation des données
        Long sessionId = 999L;
        Long userId = 1L;

        // System.out.println("=== TEST PARTICIPATE - SESSION NOT FOUND ===");
        // System.out.println("Session ID inexistant: " + sessionId);

        // Configuration des mocks - session inexistante
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Peu importe le résultat
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then - Vérification que l'exception est levée
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            sessionService.participate(sessionId, userId);
        });

        // System.out.println("Exception NotFoundException levée comme attendu");

        // Vérifications des appels
        verify(sessionRepository, times(1)).findById(sessionId);

        // Sera appelé aussi

        verify(userRepository, times(1)).findById(userId);

        // Save ne doit pas être appelé
        verify(sessionRepository, never()).save(any());
    }

    /*-------------------------- USER NOT FOUND (PARTICIPATE) ------------------------------ */

    @Test
    void shouldThrowNotFoundException_WhenUserNotExists_OnParticipate() {
        // Given - Préparation des données
        Long sessionId = 1L;
        Long userId = 999L;

        Session session = new Session();
        session.setId(sessionId);
        session.setName("Yoga Session");
        session.setUsers(new ArrayList<>());

        // System.out.println("=== TEST PARTICIPATE - USER NOT FOUND ===");
        // System.out.println("User ID inexistant: " + userId);

        // Configuration des mocks - user inexistant
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then - Vérification que l'exception est levée
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            sessionService.participate(sessionId, userId);
        });

        // System.out.println("Exception NotFoundException levée comme attendu ");

        // Vérifications des appels
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);

        // Save ne doit pas être appelé
        verify(sessionRepository, never()).save(any());
    }

    /*-------------------------- USER ALREDEAY PARTICIPATING ------------------------------ */

    @Test
    void shouldThrowBadRequestException_WhenUserAlreadyParticipates() {
        // Given - Préparation des données
        Long sessionId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Session session = new Session();
        session.setId(sessionId);
        session.setName("Yoga Session");
        session.setUsers(new ArrayList<>());

        // User DÉJÀ dans la liste
        session.getUsers().add(user);

        // System.out.println("=== TEST PARTICIPATE - ALREADY PARTICIPATING ===");
        // System.out.println("User déjà participant, participants actuels: " +
        // session.getUsers().size());

        // Configuration des mocks
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then - Vérification que l'exception est levée
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            sessionService.participate(sessionId, userId);
        });

        // System.out.println("Exception BadRequestException levée comme attendu");

        // Vérifications
        // Pas d'ajout supplémentaire
        assertEquals(1, session.getUsers().size());

        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);

        // Save ne doit pas être appelé
        verify(sessionRepository, never()).save(any());
    }

    /*-------------------------- NO LONGER PARTICIPATE (SUCCESS) ------------------------------ */

    @Test
    void shouldRemoveUserFromSession_WhenUserParticipates() {
        // Given - Préparation des données
        Long sessionId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Session session = new Session();
        session.setId(sessionId);
        session.setName("Yoga Session");
        session.setUsers(new ArrayList<>());

        // User PRÉSENT dans la liste
        session.getUsers().add(user);

        Session savedSession = new Session();
        savedSession.setId(sessionId);

        // System.out.println("=== TEST NO LONGER PARTICIPATE - SUCCESS ===");
        // System.out.println("Participants avant: " + session.getUsers().size());

        // Configuration des mocks
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(savedSession);

        // When - Exécution
        sessionService.noLongerParticipate(sessionId, userId);

        // Then - Vérifications
        // User retiré
        assertEquals(0, session.getUsers().size());
        assertFalse(session.getUsers().contains(user));

        // System.out.println("Participants après: " + session.getUsers().size());

        // Vérifications des appels
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(sessionRepository, times(1)).save(session);
    }

    /*-------------------------- NO LONGER PARTICIPATE (no session) ------------------------------ */

    @Test
    void shouldThrowNotFoundException_WhenSessionNotExists_OnNoLongerParticipate() {
        // Given - Préparation des données
        Long sessionId = 999L;
        Long userId = 1L;

        // System.out.println("=== TEST NO LONGER PARTICIPATE - SESSION NOT FOUND ===");
        // System.out.println("Session ID inexistant: " + sessionId);

        // Configuration du mock - session inexistante
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When & Then - Vérification que l'exception est levée
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            sessionService.noLongerParticipate(sessionId, userId);
        });

        // System.out.println("Exception NotFoundException levée comme attendu");

        // Vérifications des appels
        verify(sessionRepository, times(1)).findById(sessionId);

        // Save ne doit pas être appelé
        verify(sessionRepository, never()).save(any());
    }

    /*-------------------------- NO LONGER PARTICIPATE (no user) ------------------------------ */

    @Test
    void shouldThrowBadRequestException_WhenUserNotParticipating() {
        // Given - Préparation des données
        Long sessionId = 1L;

        // User qui ne participe pas
        Long userId = 999L;

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        Session session = new Session();
        session.setId(sessionId);
        session.setName("Yoga Session");
        session.setUsers(new ArrayList<>());

        // Seulement otherUser, pas userId=999
        session.getUsers().add(otherUser);

        // System.out.println("=== TEST NO LONGER PARTICIPATE - USER NOT PARTICIPATING
        // ===");
        // System.out.println("User ID " + userId + " ne participe pas à la session");
        // System.out.println("Participants actuels: " + session.getUsers().size());

        // Configuration du mock
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // When & Then - Vérification que l'exception est levée
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(sessionId, userId);
        });

        // System.out.println("Exception BadRequestException levée comme attendu");

        // Vérifications
        // Liste inchangée
        assertEquals(1, session.getUsers().size());

        // otherUser toujours présent
        assertTrue(session.getUsers().contains(otherUser));

        // Vérifications des appels
        verify(sessionRepository, times(1)).findById(sessionId);

        // Save ne doit pas être appelé
        verify(sessionRepository, never()).save(any());
    }

}
