package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockHistoryServiceTest {

    private StockHistoryRepository stockHistoryRepository;
    private ProductService productService;
    private StockHistoryService stockHistoryService;

    @BeforeEach
    void setUp() {
        stockHistoryRepository = mock(StockHistoryRepository.class);
        productService = mock(ProductService.class);
        stockHistoryService = new StockHistoryService(stockHistoryRepository, productService);
    }

    @Test
    void createStockHistories_WhenNormalCase_SavesHistories() {
        Stock stock = Stock.builder().id(1L).productId(1L).warehouse(Warehouse.builder().id(1L).build()).build();
        StockQuantityVm quantityVm = new StockQuantityVm(1L, 10L, "note");
        
        stockHistoryService.createStockHistories(List.of(stock), List.of(quantityVm));

        verify(stockHistoryRepository).saveAll(anyList());
    }

    @Test
    void getStockHistories_WhenNormalCase_ReturnsListVm() {
        StockHistory history = StockHistory.builder().productId(1L).build();
        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(anyLong(), anyLong()))
            .thenReturn(List.of(history));
        when(productService.getProduct(anyLong())).thenReturn(new ProductInfoVm(1L, "name", "sku", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(1L, 1L);

        assertThat(result.data()).hasSize(1);
    }
}
