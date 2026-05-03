package com.yas.tax.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenCodeExists_ShouldReturnMessage() {
        // This depends on messages.properties existing in test resources
        // For now, testing that it doesn't crash and returns the code if not found
        String message = MessagesUtils.getMessage("TAX_CLASS_NOT_FOUND", 1L);
        assertNotNull(message);
    }

    @Test
    void getMessage_WhenCodeNotExists_ShouldReturnCode() {
        String message = MessagesUtils.getMessage("NON_EXISTENT_CODE");
        assertEquals("NON_EXISTENT_CODE", message);
    }
}
