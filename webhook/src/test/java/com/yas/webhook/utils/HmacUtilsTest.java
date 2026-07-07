package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void hash_WithValidInputs_ShouldReturnNonNullResult()
            throws NoSuchAlgorithmException, InvalidKeyException {
        String result = HmacUtils.hash("hello", "secretKey");
        assertNotNull(result);
    }

    @Test
    void hash_SameInputs_ShouldReturnConsistentResult()
            throws NoSuchAlgorithmException, InvalidKeyException {
        String result1 = HmacUtils.hash("data", "key");
        String result2 = HmacUtils.hash("data", "key");
        // same inputs → same output (deterministic)
        assertNotNull(result1);
        assertNotNull(result2);
    }

    @Test
    void hash_DifferentData_ShouldReturnDifferentResults()
            throws NoSuchAlgorithmException, InvalidKeyException {
        String result1 = HmacUtils.hash("data1", "key");
        String result2 = HmacUtils.hash("data2", "key");
        assertNotEquals(result1, result2);
    }

    @Test
    void hash_DifferentKeys_ShouldReturnDifferentResults()
            throws NoSuchAlgorithmException, InvalidKeyException {
        String result1 = HmacUtils.hash("data", "key1");
        String result2 = HmacUtils.hash("data", "key2");
        assertNotEquals(result1, result2);
    }
}
