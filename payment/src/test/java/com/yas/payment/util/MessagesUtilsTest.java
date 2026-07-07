package com.yas.payment.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.payment.utils.MessagesUtils;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithUnknownKey_ShouldReturnKeyAsMessage() {
        // messages.properties may not define this key, so it falls back to the key itself
        String result = MessagesUtils.getMessage("some.undefined.error");
        assertEquals("some.undefined.error", result);
    }

    @Test
    void getMessage_WithUnknownKeyAndSingleArg_ShouldSubstituteArgIntoMessage() {
        String result = MessagesUtils.getMessage("Resource {} not found", "payment-provider");
        assertNotNull(result);
        assertEquals("Resource payment-provider not found", result);
    }

    @Test
    void getMessage_WithMultipleArgs_ShouldFormatAllPlaceholders() {
        String result = MessagesUtils.getMessage("Values {} and {}", "alpha", "beta");
        assertEquals("Values alpha and beta", result);
    }
}
