package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private WarehouseService warehouseService;
    private StockHistoryService stockHistoryService;
    private StockService stockService;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        warehouseService = mock(WarehouseService.class);
        stockHistoryService = mock(StockHistoryService.class);
        stockService = new StockService(warehouseRepository, stockRepository, productService, warehouseService, stockHistoryService);
    }

    @Test
    void addProductIntoWarehouse_WhenStockExists_ThrowsStockExistingException() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(true);

        assertThrows(StockExistingException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void addProductIntoWarehouse_WhenProductNotFound_ThrowsNotFoundException() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void addProductIntoWarehouse_WhenWarehouseNotFound_ThrowsNotFoundException() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(mock(ProductInfoVm.class));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void addProductIntoWarehouse_WhenNormalCase_SavesStock() {
        StockPostVm postVm = new StockPostVm(1L, 1L);
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(mock(ProductInfoVm.class));
        Warehouse warehouse = Warehouse.builder().id(1L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        stockService.addProductIntoWarehouse(List.of(postVm));

        verify(stockRepository).saveAll(anyList());
    }

    @Test
    void getStocksByWarehouseIdAndProductNameAndSku_WhenNormalCase_ReturnsStockVms() {
        ProductInfoVm productInfo = new ProductInfoVm(1L, "name", "sku", true);
        when(warehouseService.getProductWarehouse(anyLong(), anyString(), anyString(), eq(FilterExistInWhSelection.YES)))
            .thenReturn(List.of(productInfo));
        
        Stock stock = Stock.builder().id(1L).productId(1L).quantity(10L).reservedQuantity(0L)
            .warehouse(Warehouse.builder().id(1L).build()).build();
        when(stockRepository.findByWarehouseIdAndProductIdIn(anyLong(), anyList()))
            .thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(1L, "name", "sku");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).productId()).isEqualTo(1L);
    }

    /*
    @Test
    void updateProductQuantityInStock_WhenNegativeAdjustedQuantityExceedsStock_ThrowsBadRequestException() {
        StockQuantityVm quantityVm = new StockQuantityVm(1L, -20L, "inc");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));
        
        Stock stock = Stock.builder().id(1L).quantity(10L).build();
        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        assertThrows(BadRequestException.class, () -> stockService.updateProductQuantityInStock(updateVm));
    }
    */

    @Test
    void updateProductQuantityInStock_WhenNormalCase_UpdatesQuantity() {
        StockQuantityVm quantityVm = new StockQuantityVm(1L, 10L, "inc");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(quantityVm));
        
        Stock stock = Stock.builder().id(1L).quantity(10L).build();
        stock.setWarehouse(Warehouse.builder().id(1L).build());
        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(updateVm);

        assertThat(stock.getQuantity()).isEqualTo(20L);
        verify(stockRepository).saveAll(anyList());
        verify(stockHistoryService).createStockHistories(anyList(), anyList());
        verify(productService).updateProductQuantity(anyList());
    }
}
