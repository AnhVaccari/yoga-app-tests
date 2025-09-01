package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    /*---------------- AUTH EXCEPTION ----------------*/
    @Test
    void shouldSetUnauthorizedResponse_WhenAuthenticationFails() throws IOException, Throwable {
        // Given
        String errorMessage = "Invalid credentials";
        String servletPath = "/api/test";
        AuthenticationException authException = new BadCredentialsException(errorMessage);

        when(request.getServletPath()).thenReturn(servletPath);
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then - Test the main behavior, not the internal ObjectMapper details
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(request).getServletPath();
        verify(response).getOutputStream();
    }

    /*---------------- EXCEPTION ----------------*/
    @Test
    void shouldThrowIOException_WhenWritingResponse() throws IOException {
        // Given
        AuthenticationException authException = new BadCredentialsException("Unauthorized");
        when(request.getServletPath()).thenReturn("/api/test");
        when(response.getOutputStream()).thenThrow(new IOException("Stream error"));

        // When & Then - Should throw IOException
        assertThrows(IOException.class, () -> {
            authEntryPointJwt.commence(request, response, authException);
        });

        // Verify response setup was attempted
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
