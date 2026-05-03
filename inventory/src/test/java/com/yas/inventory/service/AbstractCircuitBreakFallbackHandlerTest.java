package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleBodilessFallback_ThrowsOriginalException() {
        Throwable t = new RuntimeException("error");
        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(t));
    }

    @Test
    void handleTypedFallback_ThrowsOriginalException() {
        Throwable t = new RuntimeException("error");
        assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(t));
    }
}
