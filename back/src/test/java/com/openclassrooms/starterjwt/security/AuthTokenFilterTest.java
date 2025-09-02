package com.openclassrooms.starterjwt.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Test
    void shouldAuthenticateUser_whenValidToken() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtUtils.validateJwtToken("validtoken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validtoken")).thenReturn("user@test.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void shouldNotAuthenticate_whenInvalidToken() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtUtils.validateJwtToken("invalidtoken")).thenReturn(false);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotAuthenticate_whenNoAuthorizationHeader() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateJwtToken(any());
    }

    @Test
    void shouldNotAuthenticate_whenAuthorizationHeaderWithoutBearer() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader token");

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateJwtToken(any());
    }

    @Test
    void shouldContinueFilter_whenExceptionThrown() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtUtils.validateJwtToken("validtoken")).thenThrow(new RuntimeException("JWT error"));

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
