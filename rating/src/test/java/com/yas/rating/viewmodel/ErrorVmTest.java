package com.yas.rating.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testErrorVmFullConstructor() {
        String statusCode = "400";
        String title = "Bad Request";
        String detail = "Invalid input";
        List<String> fieldErrors = List.of("field1: error1");

        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        assertEquals(statusCode, errorVm.statusCode());
        assertEquals(title, errorVm.title());
        assertEquals(detail, errorVm.detail());
        assertEquals(fieldErrors, errorVm.fieldErrors());
    }

    @Test
    void testErrorVmShortConstructor() {
        String statusCode = "404";
        String title = "Not Found";
        String detail = "Resource not found";

        ErrorVm errorVm = new ErrorVm(statusCode, title, detail);

        assertEquals(statusCode, errorVm.statusCode());
        assertEquals(title, errorVm.title());
        assertEquals(detail, errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }
}
