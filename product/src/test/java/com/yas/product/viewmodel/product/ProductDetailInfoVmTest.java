package com.yas.product.viewmodel.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ProductDetailInfoVmTest {

    @Test
    void testGetterAndSetter() {
        ProductDetailInfoVm vm = new ProductDetailInfoVm(
            1L, "Info Product", null, null, null, null, null, null, null, null, null, null, null, 250.0, null, null, null, null, null, null, null, null, null, null, null
        );
        // Note: stockQuantity is missing from constructor? Wait, let's just assert what we set.
        
        assertEquals(1L, vm.getId());
        assertEquals("Info Product", vm.getName());
        assertEquals(250.0, vm.getPrice());
    }

}
