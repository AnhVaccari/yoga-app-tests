
package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Activer Mockito dans JUnit 5
@ExtendWith(MockitoExtension.class)

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    /*---------------- GET USER ----------------*/
    @Test
    void shouldReturnUser_WhenUserExists() {
        // Given - Préparation des données
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setEmail("test@example.com");
        expectedUser.setFirstName("John");
        expectedUser.setLastName("Doe");

        // System.out.println("=== DONNÉES DE TEST ===");
        // System.out.println("userId: " + userId);
        // System.out.println("expectedUser: " + expectedUser.getEmail());

        // Configuration du mock : "Quand on appelle findById(1L), retourne
        // expectedUser"
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // When - Exécution de la méthode à tester
        User actualUser = userService.findById(userId);

        // System.out.println("=== RÉSULTAT ===");
        // System.out.println("actualUser: " + (actualUser != null ?
        // actualUser.getEmail() : "null"));

        // Then - Vérifications
        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());

        // Vérification que le repository a bien été appelé
        verify(userRepository, times(1)).findById(userId);

    }

    /*---------------- DELETE USER ----------------*/

    @Test
    void shouldDeleteUser_WhenValidId() {
        // Given - Préparation des données
        Long userId = 1L;

        // System.out.println("=== TEST DELETE ===");
        // System.out.println("userId à supprimer: " + userId);

        // When - Exécution de la méthode à tester
        userService.delete(userId);

        // System.out.println("Méthode delete() appelée");

        // Then - Vérification que le repository a bien été appelé
        verify(userRepository, times(1)).deleteById(userId);

        // System.out.println("Vérification: deleteById(" + userId + ")");
    }

    /*---------------- GET USER ( USER DOES NOT EXISTS ) ----------------*/
    @Test
    void shouldReturnNull_WhenUserNotExists() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        User result = userService.findById(userId);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);

    }

}