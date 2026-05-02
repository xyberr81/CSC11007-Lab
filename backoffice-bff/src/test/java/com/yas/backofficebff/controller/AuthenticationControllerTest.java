package com.yas.backofficebff.controller;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void whenPrincipalIsValid_thenReturnAuthenticatedUser() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn("admin_user");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(mockPrincipal);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("admin_user", response.getBody().username());
    }

    @Test
    void whenPrincipalHasNullUsername_thenReturnUserWithNullUsername() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(mockPrincipal);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody().username());
    }

    @Test
    void whenPrincipalIsNull_thenThrowIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            authenticationController.user(null);
        });
    }

    @Test
    void whenPrincipalHasDifferentUsername_thenReturnCorrectUsername() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn("another_admin");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(mockPrincipal);

        assertEquals("another_admin", response.getBody().username());
    }
}