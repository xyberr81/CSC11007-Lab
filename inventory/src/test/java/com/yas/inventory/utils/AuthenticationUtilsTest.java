package com.yas.inventory.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @Test
    void extractUserId_WhenAnonymous_ThrowsAccessDeniedException() {
        Authentication auth = mock(AnonymousAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
    }

    @Test
    void extractUserId_WhenAuthenticated_ReturnsSubject() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user-id");
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getToken()).thenReturn(jwt);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String result = AuthenticationUtils.extractUserId();
        assertThat(result).isEqualTo("user-id");
    }

    @Test
    void extractJwt_WhenNormalCase_ReturnsTokenValue() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token-value");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String result = AuthenticationUtils.extractJwt();
        assertThat(result).isEqualTo("token-value");
    }
}
