package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserVmTest {

    @Test
    void testCreateWithUsername() {
        var vm = new AuthenticatedUserVm("john_doe");
        assertEquals("john_doe", vm.username());
    }

    @Test
    void testCreateWithNullUsername() {
        var vm = new AuthenticatedUserVm(null);
        assertNull(vm.username());
    }

    @Test
    void testEquality() {
        var vm1 = new AuthenticatedUserVm("john");
        var vm2 = new AuthenticatedUserVm("john");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new AuthenticatedUserVm("john");
        var vm2 = new AuthenticatedUserVm("jane");
        assertNotEquals(vm1, vm2);
    }

    @Test
    void testToString() {
        var vm = new AuthenticatedUserVm("john_doe");
        assertTrue(vm.toString().contains("john_doe"));
    }
}