package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemVmTest {

    @Test
    void testCreateCartItem() {
        var vm = new CartItemVm(100L, 5);
        assertEquals(100L, vm.productId());
        assertEquals(5, vm.quantity());
    }

    @Test
    void testFromCartDetailVm() {
        var cartDetail = new CartDetailVm(1L, 200L, 3);
        var vm = CartItemVm.fromCartDetailVm(cartDetail);
        assertEquals(200L, vm.productId());
        assertEquals(3, vm.quantity());
    }

    @Test
    void testFromCartDetailVm_preservesQuantity() {
        var cartDetail = new CartDetailVm(5L, 999L, 10);
        var vm = CartItemVm.fromCartDetailVm(cartDetail);
        assertEquals(10, vm.quantity());
    }

    @Test
    void testEquality() {
        var vm1 = new CartItemVm(100L, 5);
        var vm2 = new CartItemVm(100L, 5);
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new CartItemVm(100L, 5);
        var vm2 = new CartItemVm(200L, 3);
        assertNotEquals(vm1, vm2);
    }
}