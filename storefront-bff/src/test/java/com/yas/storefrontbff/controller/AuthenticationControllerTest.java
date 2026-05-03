package com.yas.storefrontbff.controller;

import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import com.yas.storefrontbff.viewmodel.AuthenticatedUserVm;
import org.junit.jupiter.api.BeforeEach;
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

    // Test 1: principal == null → trả về isAuthenticated = false
    @Test
    void whenPrincipalIsNull_thenReturnUnauthenticated() {
        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAuthenticated());
        assertNull(response.getBody().authenticatedUser());
    }

    // Test 2: principal có username → trả về isAuthenticated = true + username đúng
    @Test
    void whenPrincipalIsValid_thenReturnAuthenticatedUser() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn("john_doe");

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(mockPrincipal);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAuthenticated());
        assertNotNull(response.getBody().authenticatedUser());
        assertEquals("john_doe", response.getBody().authenticatedUser().username());
    }

    // Test 3: principal có username null
    @Test
    void whenPrincipalHasNullUsername_thenReturnAuthenticatedWithNullUsername() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(mockPrincipal);

        assertTrue(response.getBody().isAuthenticated());
        assertNull(response.getBody().authenticatedUser().username());
    }
}