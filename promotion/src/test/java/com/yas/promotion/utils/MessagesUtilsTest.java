package com.yas.promotion.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithUnknownKey_ShouldReturnKeyAsMessage() {
        String result = MessagesUtils.getMessage("some.unknown.key");
        assertEquals("some.unknown.key", result);
    }

    @Test
    void getMessage_WithUnknownKeyAndSingleArg_ShouldSubstituteArg() {
        String result = MessagesUtils.getMessage("Promotion {} not found", "COUPON99");
        assertNotNull(result);
        assertEquals("Promotion COUPON99 not found", result);
    }

    @Test
    void getMessage_WithMultipleArgs_ShouldFormatAllPlaceholders() {
        String result = MessagesUtils.getMessage("Values {} and {}", "a", "b");
        assertEquals("Values a and b", result);
    }
}
