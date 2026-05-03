package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GuestUserVmTest {

    @Test
    void testCreateGuestUser() {
        var vm = new GuestUserVm("user-001", "guest@email.com", "secret123");
        assertEquals("user-001", vm.userId());
        assertEquals("guest@email.com", vm.email());
        assertEquals("secret123", vm.password());
    }

    @Test
    void testCreateWithNulls() {
        var vm = new GuestUserVm(null, null, null);
        assertNull(vm.userId());
        assertNull(vm.email());
        assertNull(vm.password());
    }

    @Test
    void testEquality() {
        var vm1 = new GuestUserVm("u1", "a@b.com", "pass");
        var vm2 = new GuestUserVm("u1", "a@b.com", "pass");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new GuestUserVm("u1", "a@b.com", "pass1");
        var vm2 = new GuestUserVm("u2", "b@c.com", "pass2");
        assertNotEquals(vm1, vm2);
    }
}