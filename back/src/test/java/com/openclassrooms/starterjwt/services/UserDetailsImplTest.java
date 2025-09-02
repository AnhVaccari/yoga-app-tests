package com.openclassrooms.starterjwt.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("password123")
                .build();
    }

    /*---------------- shouldReturnCorrectValues_WhenCreatedWithBuilder ----------------*/
    @Test
    void shouldReturnCorrectValues_WhenCreatedWithBuilder() {
        // Then
        assertEquals(1L, userDetails.getId());
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
        assertEquals(false, userDetails.getAdmin());
        assertEquals("password123", userDetails.getPassword());
    }

    /*---------------- shouldReturnEmptyAuthorities ----------------*/
    @Test
    void shouldReturnEmptyAuthorities() {
        // When
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    /*---------------- shouldReturnTrueForAllAccountFlags ----------------*/
    @Test
    void shouldReturnTrueForAllAccountFlags() {
        // Then
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    /*---------------- shouldReturnTrueForEquals_WhenSameId ----------------*/
    @Test
    void shouldReturnTrueForEquals_WhenSameId() {
        // Given
        UserDetailsImpl otherUser = UserDetailsImpl.builder()
                .id(1L) // Same ID
                .username("different@example.com")
                .firstName("Anna")
                .lastName("Smith")
                .admin(true)
                .password("differentPassword")
                .build();

        // When & Then
        assertTrue(userDetails.equals(otherUser));
        assertTrue(otherUser.equals(userDetails));
    }

    /*---------------- shouldReturnFalseForEquals_WhenDifferentId ----------------*/
    @Test
    void shouldReturnFalseForEquals_WhenDifferentId() {
        // Given
        UserDetailsImpl otherUser = UserDetailsImpl.builder()
                .id(2L) // Different ID
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("password123")
                .build();

        // When & Then
        assertFalse(userDetails.equals(otherUser));
        assertFalse(otherUser.equals(userDetails));
    }

    /*---------------- shouldReturnTrueForEquals_WhenSameObject ----------------*/
    @Test
    void shouldReturnTrueForEquals_WhenSameObject() {
        // When & Then
        assertTrue(userDetails.equals(userDetails));
    }

    /*---------------- shouldReturnFalseForEquals_WhenNull ----------------*/
    @Test
    void shouldReturnFalseForEquals_WhenNull() {
        // When & Then
        assertFalse(userDetails.equals(null));
    }

    /*---------------- shouldReturnFalseForEquals_WhenDifferentClass ----------------*/
    @Test
    void shouldReturnFalseForEquals_WhenDifferentClass() {
        // Given
        String differentObject = "not a UserDetailsImpl";

        // When & Then
        assertFalse(userDetails.equals(differentObject));
    }

    /*---------------- shouldHandleNullId_InEquals ----------------*/
    @Test
    void shouldHandleNullId_InEquals() {
        // Given
        UserDetailsImpl userWithNullId = UserDetailsImpl.builder()
                .id(null)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("password123")
                .build();

        UserDetailsImpl otherUserWithNullId = UserDetailsImpl.builder()
                .id(null)
                .username("other@example.com")
                .firstName("Anna")
                .lastName("Smith")
                .admin(true)
                .password("otherPassword")
                .build();

        // When & Then
        assertTrue(userWithNullId.equals(otherUserWithNullId)); // Both null IDs are equal
        assertFalse(userDetails.equals(userWithNullId)); // Non-null vs null
        assertFalse(userWithNullId.equals(userDetails)); // Null vs non-null
    }

    /*---------------- shouldCreateWithAdminTrue ----------------*/
    @Test
    void shouldCreateWithAdminTrue() {
        // Given
        UserDetailsImpl adminUser = UserDetailsImpl.builder()
                .id(2L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .admin(true)
                .password("adminPassword")
                .build();

        // Then
        assertTrue(adminUser.getAdmin());
    }
}