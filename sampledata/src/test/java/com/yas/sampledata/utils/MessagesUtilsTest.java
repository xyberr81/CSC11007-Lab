package com.yas.sampledata.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithUnknownKey_ShouldReturnKeyAsMessage() {
        // messages.properties is empty, so any key falls back to itself
        String result = MessagesUtils.getMessage("some.unknown.key");
        assertEquals("some.unknown.key", result);
    }

    @Test
    void getMessage_WithUnknownKeyAndArgs_ShouldFormatArgsIntoKey() {
        // key itself is treated as the pattern when not found in bundle
        String result = MessagesUtils.getMessage("Error: {} not found", "productId");
        assertNotNull(result);
        assertEquals("Error: productId not found", result);
    }

    @Test
    void getMessage_WithMultipleArgs_ShouldFormatAllPlaceholders() {
        String result = MessagesUtils.getMessage("Value {} and {}", "first", "second");
        assertEquals("Value first and second", result);
    }
}
