package com.yas.product.viewmodel.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProductViewModelsTest {

    @Test
    void testProductEsDetailVm() {
        ProductEsDetailVm vm = new ProductEsDetailVm(
                1L, "Name", "slug", 10.0, true, true, true, true, 1L, "Brand", List.of("Cat"), List.of("Attr")
        );
        
        assertEquals(1L, vm.id());
        assertEquals("Name", vm.name());
        assertEquals("slug", vm.slug());
        assertEquals(10.0, vm.price());
        assertTrue(vm.isPublished());
        assertTrue(vm.isVisibleIndividually());
        assertTrue(vm.isAllowedToOrder());
        assertTrue(vm.isFeatured());
        assertEquals(1L, vm.thumbnailMediaId());
        assertEquals("Brand", vm.brand());
        assertEquals(1, vm.categories().size());
        assertEquals(1, vm.attributes().size());
        
        // Also call equals and hashCode
        ProductEsDetailVm vm2 = new ProductEsDetailVm(
                1L, "Name", "slug", 10.0, true, true, true, true, 1L, "Brand", List.of("Cat"), List.of("Attr")
        );
        assertEquals(vm, vm2);
        assertEquals(vm.hashCode(), vm2.hashCode());
        assertNotNull(vm.toString());
    }

    @Test
    void testProductThumbnailGetVm() {
        ProductThumbnailGetVm vm = new ProductThumbnailGetVm(1L, "Name", "slug", "url", 10.0);
        assertEquals(1L, vm.id());
        assertEquals("Name", vm.name());
        assertEquals("slug", vm.slug());
        assertEquals("url", vm.thumbnailUrl());
        assertEquals(10.0, vm.price());

        ProductThumbnailGetVm vm2 = new ProductThumbnailGetVm(1L, "Name", "slug", "url", 10.0);
        assertEquals(vm, vm2);
        assertEquals(vm.hashCode(), vm2.hashCode());
        assertNotNull(vm.toString());
    }

    @Test
    void testProductGetCheckoutListVm() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(List.of(), 1, 10, 0, 0, true);
        assertNotNull(vm.productCheckoutListVms());
        assertEquals(1, vm.pageNo());
        assertEquals(10, vm.pageSize());
        assertEquals(0, vm.totalElements());
        assertEquals(0, vm.totalPages());
        assertTrue(vm.isLast());

        ProductGetCheckoutListVm vm2 = new ProductGetCheckoutListVm(List.of(), 1, 10, 0, 0, true);
        assertEquals(vm, vm2);
        assertEquals(vm.hashCode(), vm2.hashCode());
        assertNotNull(vm.toString());
    }


    @Test
    void testProductFeatureGetVm() {
        ProductFeatureGetVm vm = new ProductFeatureGetVm(List.of(), 1);
        assertNotNull(vm.productList());
    }

    @Test
    void testProductsGetVm() {
        ProductsGetVm vm = new ProductsGetVm(List.of(), 1, 2, 3, 4, true);
        assertNotNull(vm.productContent());
    }
}
