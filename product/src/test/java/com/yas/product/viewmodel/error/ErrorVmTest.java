package com.yas.product.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testErrorVm() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Error Details");
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Error Details", errorVm.detail());
    }

    @Test
    void testErrorVmWithList() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Error Details", List.of("Field1", "Field2"));
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Error Details", errorVm.detail());
        assertEquals(2, errorVm.fieldErrors().size());
    }
}
