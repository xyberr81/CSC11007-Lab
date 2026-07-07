package com.yas.promotion.repository;

import com.yas.promotion.PromotionApplication;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = PromotionApplication.class)
class PromotionRepositoryTest {

    @Autowired
    private PromotionRepository promotionRepository;

    @MockitoBean
    private ProductService productService;

    @Test
    void testFindBySlugAndIsActiveTrue() {
        Optional<Promotion> promotion = promotionRepository.findBySlugAndIsActiveTrue("non-existing-slug");
        assertNotNull(promotion);
        assertFalse(promotion.isPresent());
    }

    @Test
    void testFindByCouponCodeAndIsActiveTrue() {
        Optional<Promotion> promotion = promotionRepository.findByCouponCodeAndIsActiveTrue("non-existing-code");
        assertNotNull(promotion);
        assertFalse(promotion.isPresent());
    }
}
