package com.yas.backofficebff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserTest {

    @Test
    void testCreateWithUsername() {
        var vm = new AuthenticatedUser("admin_user");
        assertEquals("admin_user", vm.username());
    }

    @Test
    void testCreateWithNullUsername() {
        var vm = new AuthenticatedUser(null);
        assertNull(vm.username());
    }

    @Test
    void testEquality() {
        var vm1 = new AuthenticatedUser("admin");
        var vm2 = new AuthenticatedUser("admin");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new AuthenticatedUser("admin");
        var vm2 = new AuthenticatedUser("user");
        assertNotEquals(vm1, vm2);
    }

    @Test
    void testToString() {
        var vm = new AuthenticatedUser("admin_user");
        assertTrue(vm.toString().contains("admin_user"));
    }

    @Test
    void testHashCode() {
        var vm1 = new AuthenticatedUser("admin");
        var vm2 = new AuthenticatedUser("admin");
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }
}