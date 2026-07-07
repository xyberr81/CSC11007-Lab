package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithUnknownKey_ShouldReturnKeyAsMessage() {
        // messages.properties has no keys defined, so any key falls back to itself
        String result = MessagesUtils.getMessage("some.unknown.error.code");
        assertEquals("some.unknown.error.code", result);
    }

    @Test
    void getMessage_WithUnknownKeyAndSingleArg_ShouldFormatArg() {
        String result = MessagesUtils.getMessage("Resource {} not found", "webhook");
        assertNotNull(result);
        assertEquals("Resource webhook not found", result);
    }

    @Test
    void getMessage_WithUnknownKeyAndMultipleArgs_ShouldFormatAllPlaceholders() {
        String result = MessagesUtils.getMessage("Field {} has value {}", "name", "test");
        assertEquals("Field name has value test", result);
    }
}
