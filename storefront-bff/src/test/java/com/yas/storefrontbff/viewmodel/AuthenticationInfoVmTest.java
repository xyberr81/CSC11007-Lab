package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationInfoVmTest {

    @Test
    void testAuthenticatedWithUser() {
        var user = new AuthenticatedUserVm("john");
        var vm = new AuthenticationInfoVm(true, user);
        assertTrue(vm.isAuthenticated());
        assertEquals(user, vm.authenticatedUser());
    }

    @Test
    void testUnauthenticatedWithNullUser() {
        var vm = new AuthenticationInfoVm(false, null);
        assertFalse(vm.isAuthenticated());
        assertNull(vm.authenticatedUser());
    }

    @Test
    void testEquality() {
        var user = new AuthenticatedUserVm("john");
        var vm1 = new AuthenticationInfoVm(true, user);
        var vm2 = new AuthenticationInfoVm(true, user);
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new AuthenticationInfoVm(true, new AuthenticatedUserVm("john"));
        var vm2 = new AuthenticationInfoVm(false, null);
        assertNotEquals(vm1, vm2);
    }
}