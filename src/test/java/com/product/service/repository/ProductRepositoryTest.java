package com.product.service.repository;

import com.product.service.entity.ProductEntity;
import com.product.service.enums.ProductType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveShouldPersistProduct() {
        var product = buildProduct(1L, "ACC-100");

        var saved = productRepository.saveAndFlush(product);

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getCustomerId());
        assertEquals("ACC-100", saved.getProductNumber());
    }

    @Test
    void findByCustomerIdShouldReturnOnlyProductsForGivenCustomer() {
        productRepository.saveAndFlush(buildProduct(10L, "ACC-200"));
        productRepository.saveAndFlush(buildProduct(10L, "ACC-201"));
        productRepository.saveAndFlush(buildProduct(20L, "ACC-300"));

        List<ProductEntity> result = productRepository.findByCustomerId(10L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getCustomerId());
        assertEquals(10L, result.get(1).getCustomerId());
    }

    @Test
    void saveShouldFailWhenProductNumberIsDuplicated() {
        productRepository.saveAndFlush(buildProduct(30L, "ACC-500"));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(buildProduct(31L, "ACC-500"))
        );
    }

    private ProductEntity buildProduct(Long customerId, String productNumber) {
        var product = new ProductEntity();
        product.setCustomerId(customerId);
        product.setProductType(ProductType.ACCOUNT);
        product.setProductNumber(productNumber);
        product.setBalance(BigDecimal.valueOf(1000));
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }
}
