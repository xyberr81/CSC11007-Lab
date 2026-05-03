package com.yas.tax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TaxRateServiceTest {

    @Mock
    private TaxRateRepository taxRateRepository;

    @Mock
    private TaxClassRepository taxClassRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private TaxRateService taxRateService;

    private TaxRate taxRate;
    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Test Tax Class");

        taxRate = TaxRate.builder()
                .id(1L)
                .rate(10.0)
                .zipCode("12345")
                .taxClass(taxClass)
                .stateOrProvinceId(1L)
                .countryId(1L)
                .build();
    }

    @Test
    void createTaxRate_WhenTaxClassExists_ShouldSuccess() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any())).thenReturn(taxRate);

        TaxRate result = taxRateService.createTaxRate(postVm);

        assertNotNull(result);
        verify(taxRateRepository).save(any());
    }

    @Test
    void createTaxRate_WhenTaxClassNotExists_ShouldThrowException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(postVm));
    }

    @Test
    void updateTaxRate_WhenExists_ShouldSuccess() {
        TaxRatePostVm postVm = new TaxRatePostVm(20.0, "54321", 1L, 1L, 1L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);

        taxRateService.updateTaxRate(postVm, 1L);

        assertEquals(20.0, taxRate.getRate());
        assertEquals("54321", taxRate.getZipCode());
        verify(taxRateRepository).save(taxRate);
    }

    @Test
    void updateTaxRate_WhenNotExists_ShouldThrowException() {
        TaxRatePostVm postVm = new TaxRatePostVm(20.0, "54321", 1L, 1L, 1L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(postVm, 1L));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);

        taxRateService.delete(1L);

        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        when(taxRateRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.delete(1L));
    }

    @Test
    void findById_WhenExists_ShouldReturnVm() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertNotNull(result);
        assertEquals(10.0, result.rate());
    }

    @Test
    void findAll_ShouldReturnList() {
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void getPageableTaxRates_ShouldReturnList() {
        Page<TaxRate> page = new PageImpl<>(List.of(taxRate));
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);

        StateOrProvinceAndCountryGetNameVm nameVm = new StateOrProvinceAndCountryGetNameVm(1L, "State", "Country");
        when(locationService.getStateOrProvinceAndCountryNames(anyList())).thenReturn(List.of(nameVm));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertNotNull(result);
        assertEquals(1, result.taxRateGetDetailContent().size());
        assertEquals("State", result.taxRateGetDetailContent().get(0).stateOrProvinceName());
    }

    @Test
    void getPageableTaxRates_WhenEmptyList_ShouldReturnEmptyContent() {
        Page<TaxRate> page = new PageImpl<>(List.of());
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertNotNull(result);
        assertEquals(0, result.taxRateGetDetailContent().size());
    }

    @Test
    void taxRateVm_fromModel_ShouldMapCorrectly() {
        TaxRateVm vm = TaxRateVm.fromModel(taxRate);
        assertEquals(taxRate.getId(), vm.id());
        assertEquals(taxRate.getRate(), vm.rate());
    }

    @Test
    void getTaxPercent_ShouldReturnRate() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "123", 1L)).thenReturn(10.0);
        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "123");
        assertEquals(10.0, result);
    }

    @Test
    void getTaxPercent_WhenNull_ShouldReturnZero() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "123", 1L)).thenReturn(null);
        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "123");
        assertEquals(0.0, result);
    }

    @Test
    void getBulkTaxRate_ShouldReturnList() {
        when(taxRateRepository.getBatchTaxRates(any(), any(), any(), any())).thenReturn(List.of(taxRate));
        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "123");
        assertEquals(1, result.size());
    }
}
