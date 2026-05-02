package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenResponseVmTest {

    @Test
    void testCreateTokenResponse() {
        var vm = new TokenResponseVm("access-token-xyz", "refresh-token-abc");
        assertEquals("access-token-xyz", vm.accessToken());
        assertEquals("refresh-token-abc", vm.refreshToken());
    }

    @Test
    void testCreateWithNullTokens() {
        var vm = new TokenResponseVm(null, null);
        assertNull(vm.accessToken());
        assertNull(vm.refreshToken());
    }

    @Test
    void testEquality() {
        var vm1 = new TokenResponseVm("acc", "ref");
        var vm2 = new TokenResponseVm("acc", "ref");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new TokenResponseVm("acc1", "ref1");
        var vm2 = new TokenResponseVm("acc2", "ref2");
        assertNotEquals(vm1, vm2);
    }
}