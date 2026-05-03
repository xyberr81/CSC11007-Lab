package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CartGetDetailVmTest {

    @Test
    void testCreateWithCartDetails() {
        var details = List.of(new CartDetailVm(1L, 100L, 2));
        var vm = new CartGetDetailVm(10L, "customer-01", details);
        assertEquals(10L, vm.id());
        assertEquals("customer-01", vm.customerId());
        assertEquals(1, vm.cartDetails().size());
    }

    @Test
    void testCreateWithEmptyCartDetails() {
        var vm = new CartGetDetailVm(1L, "customer-01", List.of());
        assertTrue(vm.cartDetails().isEmpty());
    }

    @Test
    void testCreateWithNullCustomerId() {
        var vm = new CartGetDetailVm(1L, null, List.of());
        assertNull(vm.customerId());
    }

    @Test
    void testEquality() {
        var details = List.of(new CartDetailVm(1L, 100L, 2));
        var vm1 = new CartGetDetailVm(1L, "customer-01", details);
        var vm2 = new CartGetDetailVm(1L, "customer-01", details);
        assertEquals(vm1, vm2);
    }
}