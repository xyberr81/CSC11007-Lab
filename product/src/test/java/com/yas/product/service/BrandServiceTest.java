package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewmodel.brand.BrandListGetVm;
import com.yas.product.viewmodel.brand.BrandPostVm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    // Retrieve a paginated list of brands successfully
    @Test
    void test_retrieve_paginated_brands_successfully() {
        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands);
        when(brandRepository.findAll(any(Pageable.class))).thenReturn(brandPage);

        BrandListGetVm result = brandService.getBrands(0, 2);

        assertEquals(2, result.brandContent().size());
        assertEquals(0, result.pageNo());
        assertEquals(2, result.pageSize());
    }

}