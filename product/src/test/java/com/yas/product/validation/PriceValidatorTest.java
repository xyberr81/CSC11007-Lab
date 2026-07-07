package com.yas.product.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriceValidatorTest {

    private PriceValidator priceValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        priceValidator = new PriceValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_WhenPriceIsZero_ShouldReturnTrue() {
        assertTrue(priceValidator.isValid(0.0, context));
    }

    @Test
    void isValid_WhenPriceIsPositive_ShouldReturnTrue() {
        assertTrue(priceValidator.isValid(99.99, context));
    }

    @Test
    void isValid_WhenPriceIsNegative_ShouldReturnFalse() {
        assertFalse(priceValidator.isValid(-0.01, context));
    }

    @Test
    void isValid_WhenPriceIsLargePositive_ShouldReturnTrue() {
        assertTrue(priceValidator.isValid(1_000_000.0, context));
    }
}
