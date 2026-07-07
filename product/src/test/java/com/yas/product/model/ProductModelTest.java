package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.model.Brand;
import com.yas.product.model.ProductOption;
import org.junit.jupiter.api.Test;

class ProductModelTest {

    @Test
    void testCategory() {
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(1L);
        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
        assertNotNull(category1.toString());
    }

    @Test
    void testProductRelated() {
        ProductRelated related1 = new ProductRelated();
        related1.setId(1L);
        ProductRelated related2 = new ProductRelated();
        related2.setId(1L);
        assertEquals(related1, related2);
        assertEquals(related1.hashCode(), related2.hashCode());
        assertNotNull(related1.toString());
    }

    @Test
    void testProduct() {
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(1L);
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
        assertNotNull(product1.toString());
    }

    @Test
    void testProductOptionCombination() {
        ProductOptionCombination combination1 = new ProductOptionCombination();
        combination1.setId(1L);
        ProductOptionCombination combination2 = new ProductOptionCombination();
        combination2.setId(1L);
        assertEquals(combination1, combination2);
        assertEquals(combination1.hashCode(), combination2.hashCode());
        assertNotNull(combination1.toString());
    }

    @Test
    void testProductOptionValue() {
        ProductOptionValue value1 = new ProductOptionValue();
        value1.setId(1L);
        ProductOptionValue value2 = new ProductOptionValue();
        value2.setId(1L);
        assertEquals(value1, value2);
        assertEquals(value1.hashCode(), value2.hashCode());
        assertNotNull(value1.toString());
    }

    @Test
    void testProductTemplate() {
        ProductTemplate template1 = new ProductTemplate();
        template1.setId(1L);
        ProductTemplate template2 = new ProductTemplate();
        template2.setId(1L);
        assertEquals(template1, template2);
        assertEquals(template1.hashCode(), template2.hashCode());
        assertNotNull(template1.toString());
    }

    @Test
    void testProductAttribute() {
        ProductAttribute attribute1 = new ProductAttribute();
        attribute1.setId(1L);
        ProductAttribute attribute2 = new ProductAttribute();
        attribute2.setId(1L);
        assertEquals(attribute1, attribute2);
        assertEquals(attribute1.hashCode(), attribute2.hashCode());
        assertNotNull(attribute1.toString());
    }

    @Test
    void testBrand() {
        Brand brand1 = new Brand();
        brand1.setId(1L);
        Brand brand2 = new Brand();
        brand2.setId(1L);
        assertEquals(brand1, brand2);
        assertEquals(brand1.hashCode(), brand2.hashCode());
        assertNotNull(brand1.toString());
    }

    @Test
    void testProductOption() {
        ProductOption option1 = new ProductOption();
        option1.setId(1L);
        ProductOption option2 = new ProductOption();
        option2.setId(1L);
        assertEquals(option1, option2);
        assertEquals(option1.hashCode(), option2.hashCode());
        assertNotNull(option1.toString());
    }
}
