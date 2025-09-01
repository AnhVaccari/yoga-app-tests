package com.openclassrooms.starterjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    private AuthEntryPointJwt authEntryPointJwt;

    @BeforeEach
    void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
    }

    /*---------------- shouldSetUnauthorizedResponse_WhenAuthenticationFails ----------------*/
    @Test
    void shouldSetUnauthorizedResponse_WhenAuthenticationFails() throws IOException, Exception {
        // Given
        String errorMessage = "Invalid credentials";
        String servletPath = "/api/test";
        AuthenticationException authException = new BadCredentialsException(errorMessage);

        when(request.getServletPath()).thenReturn(servletPath);
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(request).getServletPath();
        verify(response).getOutputStream();
        verify(outputStream).write(any(byte[].class));
    }

    /*---------------- shouldHandleIOException_WhenWritingResponse ----------------*/
    @Test
    void shouldHandleIOException_WhenWritingResponse() throws IOException, Exception {
        // Given
        AuthenticationException authException = new BadCredentialsException("Unauthorized");
        when(request.getServletPath()).thenReturn("/api/test");
        when(response.getOutputStream()).thenThrow(new IOException("Stream error"));

        // When & Then - Should not throw exception
        authEntryPointJwt.commence(request, response, authException);

        // Verify response setup was attempted
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
