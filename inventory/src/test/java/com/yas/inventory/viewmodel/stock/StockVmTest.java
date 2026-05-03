package com.yas.inventory.viewmodel.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import org.junit.jupiter.api.Test;

class StockVmTest {

    @Test
    void fromModel_WhenNormalCase_ReturnsVm() {
        Stock stock = Stock.builder()
            .id(1L)
            .productId(10L)
            .quantity(100L)
            .reservedQuantity(10L)
            .warehouse(Warehouse.builder().id(2L).build())
            .build();
        ProductInfoVm productInfo = new ProductInfoVm(10L, "Product", "SKU", true);

        StockVm vm = StockVm.fromModel(stock, productInfo);

        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.productId()).isEqualTo(10L);
        assertThat(vm.productName()).isEqualTo("Product");
        assertThat(vm.productSku()).isEqualTo("SKU");
        assertThat(vm.quantity()).isEqualTo(100L);
        assertThat(vm.reservedQuantity()).isEqualTo(10L);
        assertThat(vm.warehouseId()).isEqualTo(2L);
    }
}
