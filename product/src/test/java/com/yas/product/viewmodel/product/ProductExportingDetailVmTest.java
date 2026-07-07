package com.yas.product.viewmodel.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ProductExportingDetailVmTest {

    @Test
    void testBuilderAndGetter() {
        ProductExportingDetailVm vm = new ProductExportingDetailVm(
                1L,
                "Test Product",
                "Short Description",
                "Description",
                "Spec",
                "SKU123",
                "GTIN123",
                "test-product",
                true,
                true,
                false,
                true,
                false,
                150.0,
                null,
                null,
                null,
                null,
                null
        );

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Test Product", vm.name());
        assertEquals("Description", vm.description());
        assertEquals("Short Description", vm.shortDescription());
        assertEquals("Spec", vm.specification());
        assertEquals("SKU123", vm.sku());
        assertEquals("GTIN123", vm.gtin());
        assertEquals("test-product", vm.slug());
        assertEquals(true, vm.isAllowedToOrder());
        assertEquals(true, vm.isPublished());
        assertEquals(false, vm.isFeatured());
        assertEquals(true, vm.isVisible());
        assertEquals(false, vm.stockTrackingEnabled());
        assertEquals(150.0, vm.price());
    }

    @Test
    void testEqualsAndHashCode() {
        ProductExportingDetailVm vm1 = new ProductExportingDetailVm(1L, "P1", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        ProductExportingDetailVm vm2 = new ProductExportingDetailVm(1L, "P1", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        ProductExportingDetailVm vm3 = new ProductExportingDetailVm(2L, "P2", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
        assertNotEquals(vm1, vm3);
    }
}
