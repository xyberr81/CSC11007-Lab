package com.yas.product.viewmodel.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ProductCheckoutListVmTest {

    @Test
    void testGetterAndSetter() {
        ProductCheckoutListVm vm = ProductCheckoutListVm.builder()
                .id(1L)
                .name("Test Product")
                .price(100.0)
                .build();
        
        assertEquals(1L, vm.id());
        assertEquals("Test Product", vm.name());
        assertEquals(100.0, vm.price());
    }

    @Test
    void testEqualsAndHashCode() {
        ProductCheckoutListVm vm1 = ProductCheckoutListVm.builder().id(1L).name("Test Product").build();
        ProductCheckoutListVm vm2 = ProductCheckoutListVm.builder().id(1L).name("Test Product").build();
        ProductCheckoutListVm vm3 = ProductCheckoutListVm.builder().id(2L).name("Another Product").build();

        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
        assertNotEquals(vm1, vm3);
        assertNotEquals(vm1.hashCode(), vm3.hashCode());
    }
}
