package com.yas.rating.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class RatingTest {

    @Test
    void testEqualsAndHashCode() {
        Rating rating1 = Rating.builder().id(1L).build();
        Rating rating2 = Rating.builder().id(1L).build();
        Rating rating3 = Rating.builder().id(2L).build();

        assertEquals(rating1, rating2);
        assertNotEquals(rating1, rating3);
        assertEquals(rating1.hashCode(), rating2.hashCode());
        assertEquals(rating1.hashCode(), rating3.hashCode());
    }

    @Test
    void testEqualsSameObject() {
        Rating rating = Rating.builder().id(1L).build();
        assertEquals(rating, rating);
    }

    @Test
    void testEqualsNull() {
        Rating rating = Rating.builder().id(1L).build();
        assertNotEquals(null, rating);
    }

    @Test
    void testEqualsDifferentClass() {
        Rating rating = Rating.builder().id(1L).build();
        assertNotEquals("string", rating);
    }
}
