package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class WarehouseServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private LocationService locationService;
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        locationService = mock(LocationService.class);
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    @Test
    void findAllWarehouses_WhenNormalCase_ReturnsVms() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Wh1").build();
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Wh1");
    }

    @Test
    void getProductWarehouse_WhenNormalCase_ReturnsProductVms() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(1L));
        ProductInfoVm productInfo = new ProductInfoVm(1L, "name", "sku", false);
        when(productService.filterProducts(anyString(), anyString(), any(), any())).thenReturn(List.of(productInfo));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "name", "sku", FilterExistInWhSelection.YES);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).existInWh()).isTrue();
    }

    @Test
    void findById_WhenNotFound_ThrowsNotFoundException() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void findById_WhenNormalCase_ReturnsDetailVm() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Wh1").addressId(1L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        AddressDetailVm address = AddressDetailVm.builder().id(1L).contactName("John").build();
        when(locationService.getAddressById(1L)).thenReturn(address);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertThat(result.name()).isEqualTo("Wh1");
        assertThat(result.contactName()).isEqualTo("John");
    }

    @Test
    void create_WhenNameDuplicated_ThrowsDuplicatedException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("Wh1").build();
        when(warehouseRepository.existsByName("Wh1")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
    }

    @Test
    void create_WhenNormalCase_SavesWarehouse() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("Wh1").build();
        when(warehouseRepository.existsByName("Wh1")).thenReturn(false);
        AddressVm address = AddressVm.builder().id(1L).build();
        when(locationService.createAddress(any())).thenReturn(address);
        when(warehouseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Warehouse result = warehouseService.create(postVm);

        assertThat(result.getName()).isEqualTo("Wh1");
        assertThat(result.getAddressId()).isEqualTo(1L);
    }

    @Test
    void update_WhenNotFound_ThrowsNotFoundException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("Wh1").build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.update(postVm, 1L));
    }

    @Test
    void update_WhenNameDuplicated_ThrowsDuplicatedException() {
        Warehouse warehouse = Warehouse.builder().id(1L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        WarehousePostVm postVm = WarehousePostVm.builder().name("Wh2").build();
        when(warehouseRepository.existsByNameWithDifferentId("Wh2", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.update(postVm, 1L));
    }

    @Test
    void update_WhenNormalCase_UpdatesWarehouse() {
        Warehouse warehouse = Warehouse.builder().id(1L).addressId(1L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        WarehousePostVm postVm = WarehousePostVm.builder().name("Wh2").build();
        when(warehouseRepository.existsByNameWithDifferentId("Wh2", 1L)).thenReturn(false);

        warehouseService.update(postVm, 1L);

        assertThat(warehouse.getName()).isEqualTo("Wh2");
        verify(locationService).updateAddress(anyLong(), any());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void delete_WhenNormalCase_DeletesWarehouse() {
        Warehouse warehouse = Warehouse.builder().id(1L).addressId(1L).build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(1L);
    }

    @Test
    void getPageableWarehouses_WhenNormalCase_ReturnsListVm() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("Wh1").build();
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 10), 1);
        when(warehouseRepository.findAll(any(PageRequest.class))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertThat(result.warehouseContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}
