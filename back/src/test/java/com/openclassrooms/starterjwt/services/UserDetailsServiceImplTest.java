package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("password123");
        testUser.setAdmin(false);
    }

    /*---------------- USER EXISTS ----------------*/
    @Test
    void shouldReturnUserDetails_WhenUserExists() {
        // Given
        String username = "test@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertEquals(testUser.getEmail(), userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());

        // Verify it's a UserDetailsImpl instance with correct data
        if (userDetails instanceof UserDetailsImpl) {
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            assertEquals(testUser.getId(), userDetailsImpl.getId());
            assertEquals(testUser.getFirstName(), userDetailsImpl.getFirstName());
            assertEquals(testUser.getLastName(), userDetailsImpl.getLastName());
        }

        // Verify repository was called
        verify(userRepository, times(1)).findByEmail(username);
    }

    /*---------------- USER NOT FOUND ----------------*/
    @Test
    void shouldThrowException_WhenUserNotFound() {
        // Given
        String username = "nonexistent@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username));

        assertEquals("User Not Found with email: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(username);
    }

    /*---------------- USERNAME NULL ----------------*/
    @Test
    void shouldThrowException_WhenUsernameIsNull() {
        // Given
        String username = null;
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username));

        assertEquals("User Not Found with email: null", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(username);
    }

    /*---------------- USERNAME EMPTY ----------------*/
    @Test
    void shouldThrowException_WhenUsernameIsEmpty() {
        // Given
        String username = "";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username));

        assertEquals("User Not Found with email: ", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(username);
    }
}
