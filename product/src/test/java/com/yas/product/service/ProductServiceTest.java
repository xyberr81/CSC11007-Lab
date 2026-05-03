package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    void createProduct_withoutVariationsAndOptions_persistsMainEntities() {
        Brand brand = brand(1L, "Brand One", "brand-one");
        Category categoryOne = category(10L, "Cat One", "cat-one");
        Category categoryTwo = category(20L, "Cat Two", "cat-two");
        Product relatedProduct = product(200L, "Related", "related-slug");

        when(productRepository.findAllById(anyList())).thenAnswer(invocation -> {
            List<Long> ids = invocation.getArgument(0);
            if (ids.contains(200L)) {
                return List.of(relatedProduct);
            }
            return List.of();
        });
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            if (product.getId() == null) {
                product.setId(1L);
            }
            return product;
        });
        when(categoryRepository.findAllById(List.of(10L, 20L))).thenReturn(List.of(categoryOne, categoryTwo));
        when(productImageRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productCategoryRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRelatedRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        ProductGetDetailVm result = productService.createProduct(postVm(
            "Main Product",
            "MAIN-SLUG",
            1L,
            List.of(10L, 20L),
            "SKU-1",
            "GTIN-1",
            10.0,
            5.0,
            99.5,
            List.of(101L, 102L),
            List.of(),
            List.of(),
            List.of(),
            List.of(200L)
        ));

        assertAll(
            () -> assertEquals(1L, result.id()),
            () -> assertEquals("Main Product", result.name()),
            () -> assertEquals("main-slug", result.slug())
        );

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        ArgumentCaptor<List> categoryCaptor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).save(productCaptor.capture());
        verify(productCategoryRepository).saveAll(categoryCaptor.capture());
        assertAll(
            () -> assertEquals("main-slug", productCaptor.getValue().getSlug()),
            () -> assertEquals(1L, productCaptor.getValue().getBrand().getId()),
            () -> assertEquals(2, categoryCaptor.getValue().size())
        );
        verify(productImageRepository).saveAll(anyList());
        verify(productRelatedRepository).saveAll(anyList());
    }

    @Test
    void createProduct_whenDuplicateSlugExists_throwsDuplicatedException() {
        Product existingProduct = product(10L, "Existing", "existing-slug");
        when(productRepository.findBySlugAndIsPublishedTrue("main-slug")).thenReturn(Optional.of(existingProduct));

        assertThrows(DuplicatedException.class, () -> productService.createProduct(postVm(
            "Main Product",
            "main-slug",
            null,
            List.of(),
            "SKU-1",
            "GTIN-1",
            10.0,
            5.0,
            99.5,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        )));

        verifyNoInteractions(brandRepository, categoryRepository, productImageRepository, productCategoryRepository);
    }

    @Test
    void createProduct_whenLengthIsSmallerThanWidth_throwsBadRequestException() {
        assertThrows(BadRequestException.class, () -> productService.createProduct(postVm(
            "Main Product",
            "main-slug",
            null,
            List.of(),
            "SKU-1",
            "GTIN-1",
            1.0,
            2.0,
            99.5,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        )));
    }

    @Test
    void createProduct_withVariationsAndOptionValues_createsOptionCombinations() {
        ProductOption option = productOption(10L, "Color");
        ProductVariationPostVm variation = new ProductVariationPostVm(
            "Variant One",
            "VARIANT-ONE",
            "SKU-V1",
            "GTIN-V1",
            39.0,
            300L,
            List.of(401L),
            Map.of(10L, "Red")
        );
        when(productRepository.findAllById(anyList())).thenReturn(List.of());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            if (product.getId() == null) {
                product.setId(1L);
            }
            return product;
        });
        when(productRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productOptionRepository.findAllByIdIn(anyList())).thenAnswer(invocation -> List.of(option));
        when(productOptionValueRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productImageRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productOptionCombinationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductGetDetailVm result = productService.createProduct(postVm(
            "Main Product",
            "MAIN-SLUG",
            null,
            List.of(),
            "SKU-1",
            "GTIN-1",
            10.0,
            5.0,
            99.5,
            List.of(),
            List.of(variation),
            List.of(new ProductOptionValuePostVm(10L, "select", 1, List.of("Red"))),
            List.of(ProductOptionValueDisplay.builder()
                .productOptionId(10L)
                .displayType("select")
                .displayOrder(1)
                .value("Red")
                .build()),
            List.of()
        ));

        assertEquals(1L, result.id());
        ArgumentCaptor<List<ProductOptionCombination>> combinationCaptor = ArgumentCaptor.forClass(List.class);
        verify(productOptionCombinationRepository).saveAll(combinationCaptor.capture());
        assertEquals(1, combinationCaptor.getValue().size());
        assertEquals("Red", combinationCaptor.getValue().getFirst().getValue());
    }

    @Test
    void updateProduct_whenMainProductMissing_throwsNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(99L, putVm(
            "Updated",
            "updated-slug",
            20.0,
            true,
            true,
            true,
            true,
            true,
            1L,
            List.of(),
            "short",
            "description",
            "spec",
            "SKU-2",
            "GTIN-2",
            2.0,
            DimensionUnit.CM,
            5.0,
            4.0,
            3.0,
            "meta title",
            "meta keyword",
            "meta description",
            55L,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            1L
        )));
    }

    @Test
    void updateProduct_updatesMainEntityCategoriesImagesRelationsAndOptions() {
        Brand newBrand = brand(2L, "Brand Two", "brand-two");
        Category existingCategory = category(1L, "Old Category", "old-category");
        Category newCategory = category(2L, "New Category", "new-category");
        Product existingRelatedProduct = product(300L, "Related One", "related-one");
        Product existingProduct = product(1L, "Old Name", "old-slug");
        existingProduct.setBrand(brand(1L, "Old Brand", "old-brand"));
        existingProduct.setProductCategories(List.of(productCategory(existingProduct, existingCategory)));
        existingProduct.setProductImages(List.of(productImage(111L, existingProduct)));
        existingProduct.setRelatedProducts(new ArrayList<>());
        ProductOption option = productOption(10L, "Color");

        ProductPutVm putVm = putVm(
            "Updated Name",
            "UPDATED-SLUG",
            111.0,
            true,
            false,
            true,
            false,
            true,
            2L,
            List.of(2L),
            "Updated short",
            "Updated description",
            "Updated specification",
            "SKU-UPDATED",
            "GTIN-UPDATED",
            4.0,
            DimensionUnit.INCH,
            8.0,
            7.0,
            6.0,
            "Meta title",
            "Meta keyword",
            "Meta description",
            55L,
            List.of(222L),
            List.of(),
            List.of(new ProductOptionValuePutVm(10L, "select", 1, List.of("Red"))),
            List.of(ProductOptionValueDisplay.builder()
                .productOptionId(10L)
                .displayType("select")
                .displayOrder(1)
                .value("Red")
                .build()),
            List.of(300L),
            1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(brandRepository.findById(2L)).thenReturn(Optional.of(newBrand));
        when(categoryRepository.findAllById(List.of(2L))).thenReturn(List.of(newCategory));
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(List.of(productCategory(existingProduct, existingCategory)));
        when(productRepository.findAllById(anyList())).thenAnswer(invocation -> {
            List<Long> ids = invocation.getArgument(0);
            if (ids.contains(300L)) {
                return List.of(existingRelatedProduct);
            }
            return List.of();
        });
        when(productOptionRepository.findAllByIdIn(anyList())).thenAnswer(invocation -> List.of(option));
        when(productOptionValueRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productImageRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productCategoryRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRelatedRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        productService.updateProduct(1L, putVm);

        assertAll(
            () -> assertEquals("Updated Name", existingProduct.getName()),
            () -> assertEquals("updated-slug", existingProduct.getSlug()),
            () -> assertEquals(2L, existingProduct.getBrand().getId()),
            () -> assertEquals(111.0, existingProduct.getPrice()),
            () -> assertFalse(existingProduct.isPublished())
        );
        verify(productImageRepository).deleteByImageIdInAndProductId(List.of(111L), 1L);
        verify(productImageRepository).saveAll(anyList());
        verify(productCategoryRepository).deleteAllInBatch(anyList());
        verify(productCategoryRepository).saveAll(anyList());
        verify(productRelatedRepository).saveAll(anyList());
        verify(productOptionValueRepository).deleteAllByProductId(1L);
        verify(productOptionValueRepository).saveAll(anyList());
    }

    @Test
    void updateMainProductFromVm_mapsFields() {
        Product product = product(1L, "Old", "old-slug");

        productService.updateMainProductFromVm(putVm(
            "Updated",
            "UPDATED-SLUG",
            55.5,
            true,
            false,
            true,
            false,
            true,
            null,
            List.of(),
            "short",
            "description",
            "spec",
            "SKU-2",
            "GTIN-2",
            2.0,
            DimensionUnit.INCH,
            9.0,
            8.0,
            7.0,
            "Meta title",
            "Meta keyword",
            "Meta description",
            10L,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            1L
        ), product);

        assertAll(
            () -> assertEquals("Updated", product.getName()),
            () -> assertEquals("updated-slug", product.getSlug()),
            () -> assertEquals(55.5, product.getPrice()),
            () -> assertEquals(DimensionUnit.INCH, product.getDimensionUnit()),
            () -> assertEquals(1L, product.getTaxClassId())
        );
    }

    @Test
    void setProductImages_returnsAddedImagesAndDeletesRemovedOnes() {
        Product product = product(1L, "Product", "product-slug");
        product.setProductImages(List.of(
            productImage(111L, product),
            productImage(222L, product)
        ));

        List<ProductImage> result = productService.setProductImages(List.of(222L, 333L), product);

        assertEquals(1, result.size());
        assertEquals(333L, result.getFirst().getImageId());
        verify(productImageRepository).deleteByImageIdInAndProductId(List.of(111L), 1L);
    }

    @Test
    void getProductById_mapsDetailAndMedia() {
        Product parent = product(90L, "Parent", "parent-slug");
        Product product = product(1L, "Product", "product-slug");
        product.setShortDescription("short");
        product.setDescription("description");
        product.setSpecification("spec");
        product.setSku("SKU-1");
        product.setGtin("GTIN-1");
        product.setAllowedToOrder(true);
        product.setPublished(true);
        product.setFeatured(true);
        product.setVisibleIndividually(true);
        product.setStockTrackingEnabled(true);
        product.setWeight(1.5);
        product.setDimensionUnit(DimensionUnit.CM);
        product.setLength(10.0);
        product.setWidth(5.0);
        product.setHeight(3.0);
        product.setPrice(99.0);
        product.setBrand(brand(10L, "Brand One", "brand-one"));
        product.setThumbnailMediaId(55L);
        product.setTaxClassId(7L);
        product.setParent(parent);
        product.setProductImages(List.of(
            productImage(101L, product),
            productImage(102L, product)
        ));
        product.setProductCategories(List.of(
            productCategory(product, category(11L, "Cat One", "cat-one")),
            productCategory(product, category(12L, "Cat Two", "cat-two"))
        ));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(55L)).thenReturn(media(55L, "thumb-url"));
        when(mediaService.getMedia(101L)).thenReturn(media(101L, "image-101"));
        when(mediaService.getMedia(102L)).thenReturn(media(102L, "image-102"));

        ProductDetailVm result = productService.getProductById(1L);

        assertAll(
            () -> assertEquals(1L, result.id()),
            () -> assertEquals("Product", result.name()),
            () -> assertEquals(10L, result.brandId()),
            () -> assertEquals(2, result.categories().size()),
            () -> assertEquals("thumb-url", result.thumbnailMedia().url()),
            () -> assertEquals(2, result.productImageMedias().size()),
            () -> assertEquals(90L, result.parentId()),
            () -> assertEquals(7L, result.taxClassId())
        );
    }

    @Test
    void getProductDetail_groupsAttributesAndMapsMedia() {
        Product product = product(1L, "Product", "product-slug");
        product.setBrand(brand(10L, "Brand One", "brand-one"));
        product.setThumbnailMediaId(55L);
        product.setProductImages(List.of(productImage(101L, product)));
        product.setProductCategories(List.of(productCategory(product, category(11L, "Cat One", "cat-one"))));

        ProductAttributeGroup specsGroup = new ProductAttributeGroup();
        specsGroup.setId(1L);
        specsGroup.setName("Specs");

        ProductAttributeValue groupedValue = attributeValue(product, "Color", specsGroup, "Red");
        ProductAttributeValue noneGroupValue = attributeValue(product, "Material", null, "Cotton");
        product.setAttributeValues(new ArrayList<>(List.of(groupedValue, noneGroupValue)));

        when(productRepository.findBySlugAndIsPublishedTrue("product-slug")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(55L)).thenReturn(media(55L, "thumb-url"));
        when(mediaService.getMedia(101L)).thenReturn(media(101L, "image-101"));

        ProductDetailGetVm result = productService.getProductDetail("product-slug");

        assertAll(
            () -> assertEquals(1L, result.id()),
            () -> assertEquals("Brand One", result.brandName()),
            () -> assertEquals("thumb-url", result.thumbnailMediaUrl()),
            () -> assertEquals(1, result.productImageMediaUrls().size()),
            () -> assertEquals(2, result.productAttributeGroups().size())
        );
        assertTrue(result.productAttributeGroups().stream().anyMatch(group -> group.name().equals("Specs")));
        assertTrue(result.productAttributeGroups().stream().anyMatch(group -> group.name().equals("None group")));
    }

    @Test
    void getProductVariationsByParentId_returnsOnlyPublishedVariants() {
        Product parent = product(1L, "Parent", "parent-slug");
        parent.setHasOptions(true);

        Product publishedVariant = product(2L, "Variant One", "variant-one");
        publishedVariant.setParent(parent);
        publishedVariant.setPublished(true);
        publishedVariant.setThumbnailMediaId(77L);
        publishedVariant.setProductImages(List.of(productImage(201L, publishedVariant)));

        Product unpublishedVariant = product(3L, "Variant Two", "variant-two");
        unpublishedVariant.setParent(parent);
        unpublishedVariant.setPublished(false);

        parent.setProducts(List.of(publishedVariant, unpublishedVariant));

        ProductOption optionOne = productOption(10L, "Color");
        ProductOption optionTwo = productOption(20L, "Size");
        when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(productOptionCombinationRepository.findAllByProduct(publishedVariant)).thenReturn(List.of(
            combination(publishedVariant, optionOne, 1, "Red"),
            combination(publishedVariant, optionTwo, 2, "M")
        ));
        when(mediaService.getMedia(77L)).thenReturn(media(77L, "thumb-url"));
        when(mediaService.getMedia(201L)).thenReturn(media(201L, "image-201"));

        List<ProductVariationGetVm> result = productService.getProductVariationsByParentId(1L);

        assertEquals(1, result.size());
        assertAll(
            () -> assertEquals(2L, result.getFirst().id()),
            () -> assertEquals("thumb-url", result.getFirst().thumbnail().url()),
            () -> assertEquals(1, result.getFirst().productImages().size()),
            () -> assertEquals(2, result.getFirst().options().size())
        );
    }

    @Test
    void deleteProduct_softDeletesAndRemovesOptionCombinations() {
        Product parent = product(10L, "Parent", "parent-slug");
        Product product = product(1L, "Variant", "variant-slug");
        product.setParent(parent);
        ProductOption option = productOption(11L, "Color");
        ProductOptionCombination combination = combination(product, option, 1, "Red");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(product)).thenReturn(List.of(combination));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.deleteProduct(1L);

        assertFalse(product.isPublished());
        verify(productOptionCombinationRepository).deleteAll(List.of(combination));
        verify(productRepository).save(product);
    }

    @Test
    void getProductSlug_returnsParentSlugForVariantAndOwnSlugForRoot() {
        Product root = product(1L, "Root", "root-slug");
        Product variant = product(2L, "Variant", "variant-slug");
        variant.setParent(root);

        when(productRepository.findById(2L)).thenReturn(Optional.of(variant));
        when(productRepository.findById(1L)).thenReturn(Optional.of(root));

        ProductSlugGetVm variantSlug = productService.getProductSlug(2L);
        ProductSlugGetVm rootSlug = productService.getProductSlug(1L);

        assertAll(
            () -> assertEquals("root-slug", variantSlug.slug()),
            () -> assertEquals(2L, variantSlug.productVariantId()),
            () -> assertEquals("root-slug", rootSlug.slug()),
            () -> assertEquals(null, rootSlug.productVariantId())
        );
    }

    @Test
    void getProductsByBrand_returnsThumbnailList() {
        Brand brand = brand(1L, "Brand One", "brand-one");
        Product product = product(1L, "Product", "product-slug");
        product.setThumbnailMediaId(55L);

        when(brandRepository.findBySlug("brand-slug")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(mediaService.getMedia(55L)).thenReturn(media(55L, "thumb-url"));

        List<ProductThumbnailVm> result = productService.getProductsByBrand("brand-slug");

        assertEquals(1, result.size());
        assertEquals("thumb-url", result.getFirst().thumbnailUrl());
    }

    @Test
    void getProductsFromCategory_returnsPagedThumbnails() {
        Category category = category(1L, "Category One", "category-one");
        Product product = product(1L, "Product", "product-slug");
        product.setThumbnailMediaId(55L);
        ProductCategory productCategory = productCategory(product, category);
        Page<ProductCategory> page = new PageImpl<>(List.of(productCategory), PageRequest.of(0, 10), 1);

        when(categoryRepository.findBySlug("category-slug")).thenReturn(Optional.of(category));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class))).thenReturn(page);
        when(mediaService.getMedia(55L)).thenReturn(media(55L, "thumb-url"));

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "category-slug");

        assertAll(
            () -> assertEquals(1, result.productContent().size()),
            () -> assertEquals(0, result.pageNo()),
            () -> assertEquals(10, result.pageSize()),
            () -> assertEquals(1, result.totalElements()),
            () -> assertEquals("thumb-url", result.productContent().getFirst().thumbnailUrl())
        );
    }

    @Test
    void getProductsWithFilter_trimsInputsAndMapsPage() {
        Product product = product(1L, "Product", "product-slug");
        product.setThumbnailMediaId(55L);
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 5), 1);

        when(productRepository.getProductsWithFilter("shoe", "Nike", PageRequest.of(0, 5))).thenReturn(page);

        ProductListGetVm result = productService.getProductsWithFilter(0, 5, "  Shoe  ", "  Nike  ");

        assertAll(
            () -> assertEquals(1, result.productContent().size()),
            () -> assertEquals(0, result.pageNo()),
            () -> assertEquals(5, result.pageSize()),
            () -> assertEquals(1, result.totalElements())
        );
    }

    private ProductPostVm postVm(
        String name,
        String slug,
        Long brandId,
        List<Long> categoryIds,
        String sku,
        String gtin,
        Double length,
        Double width,
        Double price,
        List<Long> productImageIds,
        List<ProductVariationPostVm> variations,
        List<ProductOptionValuePostVm> productOptionValues,
        List<ProductOptionValueDisplay> productOptionValueDisplays,
        List<Long> relatedProductIds
    ) {
        return new ProductPostVm(
            name,
            slug,
            brandId,
            categoryIds,
            "short description",
            "description",
            "specification",
            sku,
            gtin,
            2.0,
            DimensionUnit.CM,
            length,
            width,
            3.0,
            price,
            true,
            true,
            true,
            true,
            true,
            "meta title",
            "meta keyword",
            "meta description",
            55L,
            productImageIds,
            variations,
            productOptionValues,
            productOptionValueDisplays,
            relatedProductIds,
            1L
        );
    }

    private ProductPutVm putVm(
        String name,
        String slug,
        Double price,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean isVisibleIndividually,
        Boolean stockTrackingEnabled,
        Long brandId,
        List<Long> categoryIds,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        Double weight,
        DimensionUnit dimensionUnit,
        Double length,
        Double width,
        Double height,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        Long thumbnailMediaId,
        List<Long> productImageIds,
        List<ProductVariationPutVm> variations,
        List<ProductOptionValuePutVm> productOptionValues,
        List<ProductOptionValueDisplay> productOptionValueDisplays,
        List<Long> relatedProductIds,
        Long taxClassId
    ) {
        return new ProductPutVm(
            name,
            slug,
            price,
            isAllowedToOrder,
            isPublished,
            isFeatured,
            isVisibleIndividually,
            stockTrackingEnabled,
            brandId,
            categoryIds,
            shortDescription,
            description,
            specification,
            sku,
            gtin,
            weight,
            dimensionUnit,
            length,
            width,
            height,
            metaTitle,
            metaKeyword,
            metaDescription,
            thumbnailMediaId,
            productImageIds,
            variations,
            productOptionValues,
            productOptionValueDisplays,
            relatedProductIds,
            taxClassId
        );
    }

    private Product product(Long id, String name, String slug) {
        return Product.builder()
            .id(id)
            .name(name)
            .slug(slug)
            .productCategories(new ArrayList<>())
            .productImages(new ArrayList<>())
            .relatedProducts(new ArrayList<>())
            .products(new ArrayList<>())
            .build();
    }

    private Brand brand(Long id, String name, String slug) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setSlug(slug);
        return brand;
    }

    private Category category(Long id, String name, String slug) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSlug(slug);
        return category;
    }

    private ProductOption productOption(Long id, String name) {
        ProductOption option = new ProductOption();
        option.setId(id);
        option.setName(name);
        return option;
    }

    private ProductCategory productCategory(Product product, Category category) {
        return ProductCategory.builder()
            .product(product)
            .category(category)
            .build();
    }

    private ProductImage productImage(Long imageId, Product product) {
        return ProductImage.builder()
            .imageId(imageId)
            .product(product)
            .build();
    }

    private ProductOptionCombination combination(Product product, ProductOption option, int displayOrder, String value) {
        return ProductOptionCombination.builder()
            .product(product)
            .productOption(option)
            .displayOrder(displayOrder)
            .value(value)
            .build();
    }

    private ProductAttributeValue attributeValue(
        Product product,
        String attributeName,
        ProductAttributeGroup group,
        String value
    ) {
        ProductAttribute attribute = ProductAttribute.builder()
            .name(attributeName)
            .productAttributeGroup(group)
            .build();
        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setProduct(product);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue(value);
        return attributeValue;
    }

    private NoFileMediaVm media(Long id, String url) {
        return new NoFileMediaVm(id, "caption", "file-name", "image/png", url);
    }
}