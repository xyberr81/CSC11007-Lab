package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartDetailVmTest {

    @Test
    void testCreateCartDetail() {
        var vm = new CartDetailVm(1L, 100L, 3);
        assertEquals(1L, vm.id());
        assertEquals(100L, vm.productId());
        assertEquals(3, vm.quantity());
    }

    @Test
    void testEquality() {
        var vm1 = new CartDetailVm(1L, 100L, 3);
        var vm2 = new CartDetailVm(1L, 100L, 3);
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new CartDetailVm(1L, 100L, 3);
        var vm2 = new CartDetailVm(2L, 200L, 5);
        assertNotEquals(vm1, vm2);
    }

    @Test
    void testNullId() {
        var vm = new CartDetailVm(null, 100L, 1);
        assertNull(vm.id());
    }
}