package com.yas.product.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProductConverterTest {

    @Test
    void toSlug_NormalInput_ShouldLowercaseAndReplaceSpaces() {
        assertEquals("hello-world", ProductConverter.toSlug("Hello World"));
    }

    @Test
    void toSlug_InputWithSpecialChars_ShouldReplaceWithDash() {
        // "!" becomes a dash at the end, and the converter doesn't strip trailing dashes
        assertEquals("iphone-14-pro-", ProductConverter.toSlug("iPhone 14 Pro!"));
    }

    @Test
    void toSlug_InputWithLeadingDash_ShouldTrimLeadingDash() {
        // Characters before first letter become "-", which gets trimmed
        assertEquals("product", ProductConverter.toSlug("  product"));
    }

    @Test
    void toSlug_InputWithMultipleConsecutiveDashes_ShouldCollapseToOne() {
        assertEquals("a-b", ProductConverter.toSlug("a---b"));
    }

    @Test
    void toSlug_AlreadyValidSlug_ShouldReturnUnchanged() {
        assertEquals("my-product-slug", ProductConverter.toSlug("my-product-slug"));
    }

    @ParameterizedTest
    @CsvSource({
        "Nike Shoes 2024, nike-shoes-2024",
        "Áo thun, o-thun", // "Á" becomes "-" which is trimmed because it's leading
        "UPPER CASE, upper-case"
    })
    void toSlug_ParameterizedCases(String input, String expected) {
        assertEquals(expected, ProductConverter.toSlug(input));
    }
}
