package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductOptionValueRepository productOptionValueRepository;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetProductSlug_whenProductNotFound_shouldThrowNotFoundException() {
        Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductSlug(productId));
    }

    @Test
    void testGetProductSlug_whenMainProduct_shouldReturnOwnSlug() {
        Long productId = 1L;
        Product product = Product.builder().id(productId).slug("main-product").build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductSlugGetVm result = productService.getProductSlug(productId);

        assertEquals("main-product", result.slug());
        assertNull(result.productVariantId());
    }

    @Test
    void testGetProductSlug_whenVariantProduct_shouldReturnParentSlugAndVariantId() {
        Long variantId = 2L;
        Product parent = Product.builder().id(1L).slug("parent-product").build();
        Product variant = Product.builder().id(variantId).slug("variant-product").parent(parent).build();
        when(productRepository.findById(variantId)).thenReturn(Optional.of(variant));

        ProductSlugGetVm result = productService.getProductSlug(variantId);

        assertEquals("parent-product", result.slug());
        assertEquals(variantId, result.productVariantId());
    }

    @Test
    void testUpdateProductQuantity_shouldUpdateAllProvidedProducts() {
        Product product1 = Product.builder().id(1L).stockQuantity(10L).stockTrackingEnabled(true).build();
        Product product2 = Product.builder().id(2L).stockQuantity(8L).stockTrackingEnabled(true).build();

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product1, product2));

        productService.updateProductQuantity(List.of(
            new ProductQuantityPostVm(1L, 5L),
            new ProductQuantityPostVm(2L, 12L)
        ));

        ArgumentCaptor<List<Product>> captor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());

        Map<Long, Product> savedById = captor.getValue().stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
        assertEquals(5L, savedById.get(1L).getStockQuantity());
        assertEquals(12L, savedById.get(2L).getStockQuantity());
    }

    @Test
    void testSubtractStockQuantity_shouldMergeDuplicatesAndNeverGoNegative() {
        Product tracked1 = Product.builder().id(1L).stockQuantity(10L).stockTrackingEnabled(true).build();
        Product tracked2 = Product.builder().id(2L).stockQuantity(3L).stockTrackingEnabled(true).build();
        Product notTracked = Product.builder().id(3L).stockQuantity(4L).stockTrackingEnabled(false).build();

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(tracked1, tracked2, notTracked));

        productService.subtractStockQuantity(List.of(
            new ProductQuantityPutVm(1L, 4L),
            new ProductQuantityPutVm(1L, 9L),
            new ProductQuantityPutVm(2L, 5L),
            new ProductQuantityPutVm(3L, 2L)
        ));

        ArgumentCaptor<List<Product>> captor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());

        Map<Long, Product> savedById = captor.getValue().stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
        assertEquals(0L, savedById.get(1L).getStockQuantity());
        assertEquals(0L, savedById.get(2L).getStockQuantity());
        assertEquals(4L, savedById.get(3L).getStockQuantity());
    }

    @Test
    void testRestoreStockQuantity_shouldMergeDuplicatesAndIncreaseStock() {
        Product tracked = Product.builder().id(1L).stockQuantity(10L).stockTrackingEnabled(true).build();

        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(tracked));

        productService.restoreStockQuantity(List.of(
            new ProductQuantityPutVm(1L, 2L),
            new ProductQuantityPutVm(1L, 3L)
        ));

        ArgumentCaptor<List<Product>> captor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());

        Product savedProduct = captor.getValue().getFirst();
        assertEquals(15L, savedProduct.getStockQuantity());
    }
}