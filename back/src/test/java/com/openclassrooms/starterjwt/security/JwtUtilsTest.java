package com.openclassrooms.starterjwt.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Forcer le secret et l'expiration pour les tests
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60000); // 60 secondes
    }

    @Test
    void shouldGenerateValidToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "test@example.com", "John", "Doe", false, "password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String username = "test@example.com";

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        String extracted = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(username, extracted);
    }

    @Test
    void shouldValidateValidToken() {
        String token = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void shouldInvalidateExpiredToken() {
        String token = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date(System.currentTimeMillis() - 120000))
                .setExpiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void shouldInvalidateTokenWithInvalidSignature() {
        String token = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void shouldInvalidateMalformedToken() {
        String malformedToken = "this.is.not.a.jwt";

        assertFalse(jwtUtils.validateJwtToken(malformedToken));
    }
}
