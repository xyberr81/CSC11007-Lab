package com.yas.tax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
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
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaxClassServiceTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private TaxClassService taxClassService;

    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Test Tax Class");
    }

    @Test
    void findAllTaxClasses_ShouldReturnList() {
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertEquals(1, result.size());
        assertEquals("Test Tax Class", result.get(0).name());
    }

    @Test
    void findById_WhenExists_ShouldReturnVm() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Tax Class", result.name());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.findById(1L));
    }

    @Test
    void create_WhenValid_ShouldSuccess() {
        TaxClassPostVm postVm = new TaxClassPostVm("tax-id", "New Tax");
        when(taxClassRepository.existsByName("New Tax")).thenReturn(false);
        when(taxClassRepository.save(any())).thenReturn(taxClass);

        TaxClass result = taxClassService.create(postVm);

        assertNotNull(result);
        verify(taxClassRepository).save(any());
    }

    @Test
    void create_WhenDuplicate_ShouldThrowException() {
        TaxClassPostVm postVm = new TaxClassPostVm("tax-id", "New Tax");
        when(taxClassRepository.existsByName("New Tax")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.create(postVm));
    }

    @Test
    void update_WhenValid_ShouldSuccess() {
        TaxClassPostVm postVm = new TaxClassPostVm("tax-id", "Updated Tax");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated Tax", 1L)).thenReturn(false);

        taxClassService.update(postVm, 1L);

        assertEquals("Updated Tax", taxClass.getName());
        verify(taxClassRepository).save(taxClass);
    }

    @Test
    void update_WhenDuplicate_ShouldThrowException() {
        TaxClassPostVm postVm = new TaxClassPostVm("tax-id", "Updated Tax");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated Tax", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.update(postVm, 1L));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxClassService.delete(1L));
    }

    @Test
    void getPageableTaxClasses_ShouldReturnList() {
        Page<TaxClass> page = new PageImpl<>(List.of(taxClass));
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertNotNull(result);
        assertEquals(1, result.taxClassContent().size());
    }
}
