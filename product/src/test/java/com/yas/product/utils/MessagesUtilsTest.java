package com.yas.product.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithUnknownKey_ShouldReturnKeyAsMessage() {
        String result = MessagesUtils.getMessage("some.unknown.product.key");
        assertEquals("some.unknown.product.key", result);
    }

    @Test
    void getMessage_WithUnknownKeyAndSingleArg_ShouldSubstituteArg() {
        String result = MessagesUtils.getMessage("Product {} not found", "42");
        assertNotNull(result);
        assertEquals("Product 42 not found", result);
    }

    @Test
    void getMessage_WithMultipleArgs_ShouldFormatAllPlaceholders() {
        String result = MessagesUtils.getMessage("Brand {} and Category {}", "Nike", "Shoes");
        assertEquals("Brand Nike and Category Shoes", result);
    }
}
